
package org.shappell.ahocorasick;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.shappell.ahocorasick.entity.GenericEntity;
import org.shappell.ahocorasick.entity.IEntity;
import org.shappell.ahocorasick.entity.StringEntity;
import org.shappell.ahocorasick.treenodes.FullTreeNode;
import org.shappell.ahocorasick.treenodes.ITreeNode;
import org.shappell.ahocorasick.treenodes.Result1Transition1TreeNode;
import org.shappell.ahocorasick.treenodes.TreeNodeBase;
import org.shappell.ahocorasick.util.AsciiUtils;

/**
 * 
 * @author shappell
 * 
 */
public class EntityDictionary {
	private static final Logger logger = Logger
			.getLogger(EntityDictionary.class);

	private ITreeNode root;

	private boolean lowercase;

	private TObjectIntHashMap<String> termHistogram;

	private int totalTermCount;

	private boolean verbose = false;

	public EntityDictionary() {
		lowercase = true;
		termHistogram = new TObjectIntHashMap<String>();
		totalTermCount = 0;
	}

	public void optimize() {
		logger.info("optimize()");
		long start = System.currentTimeMillis();
		BuildFailureLinksOptimized();
		long end = System.currentTimeMillis();
		logger.info("optimize() took(msecs): " + (end - start));
	}

	public List<StringSearchResult> FindAll(String text) {
		logger.debug("FindAll(): text=" + text);
		List<StringSearchResult> ret = new ArrayList<StringSearchResult>();
		ITreeNode ptr = root;
		int index = 0;

		char prevChar = ' ';
		char curChar;
		int termPosition = 1;

		while (index < text.length()) {
			if (this.lowercase)
				curChar = Character.toLowerCase(AsciiUtils.convertNonAscii(text
						.charAt(index)));
			else
				curChar = AsciiUtils.convertNonAscii(text.charAt(index));

			if (verbose && logger.isDebugEnabled()) {
				logger.debug("FindAll(char = '" + curChar + "')");
			}

			// Need to treat any consecutive whitespace chars as a single
			// whitespace
			if (Character.isWhitespace(curChar)) {
				curChar = ' ';
				if (prevChar == ' ') {
					index++;
					continue;
				}
				termPosition++;
			}

			ITreeNode trans = null;
			while (trans == null) {
				trans = ptr.getTransition(curChar);

				if (ptr == root)
					break;

				if (trans == null)
					ptr = ptr.getFailure();
			}

			if (trans != null)
				ptr = trans;

			if (ptr.getResults() != null) {
				for (EntityResult foundResult : ptr.getResults()) {
					IEntity foundEntity = null;

					foundEntity = foundResult.getEntity();

					String matchedString = text.substring(
							index - foundResult.getDepth() + 1, index + 1);
					StringSearchResult potentialMatch = new StringSearchResult(
							index - foundResult.getDepth() + 1, index + 1,
							matchedString, foundEntity, termPosition);

					boolean isTokenSeparateWord = isTokenSeparateWord(
							potentialMatch, text);

					if (isTokenSeparateWord) {
						ret.add(potentialMatch);
						if (logger.isDebugEnabled()) {
							logger.debug("found valid match = "
									+ potentialMatch);
						}
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("match failed 'isTokenSeparateWord' check = "
									+ potentialMatch);
						}
					}
				}
			}

			prevChar = curChar;
			index++;
		}

		// Lets sort results by startIndex
		// most of the time it will be so 1 quick check for that
		int curStartIndex = 0;
		boolean sorted = true;
		for (StringSearchResult result : ret) {
			if (result.getStart() < curStartIndex) {
				sorted = false;
				break;
			} else {
				curStartIndex = result.getStart();
			}
		}

		if (!sorted) {
			Collections.sort(ret, StringSearchResult.getIndexSorter());
		}

		return ret;
	}

	public void AddEntities(List<? extends IEntity> newEntities) {
		BuildTree(newEntities);
	}

	public void AddStringEntities(List<String> newEntities) {
		logger.debug("AddStringEntities()");
		for (String entityName : newEntities) {
			StringEntity entity = new StringEntity(entityName);
			AddEntity(entity);
		}
	}

	public void AddEntity(IEntity entity) {
		if (logger.isDebugEnabled())
			logger.debug("AddEntity(): entity=" + entity);

		// Build keyword tree and transition function
		if (this.root == null) {
			root = new FullTreeNode(null, ' ');
		}

		if (entity.getSynonyms() == null) {
			AddNodeWithStringOptimized(entity.getName(), entity, true);
		} else {
			for (String synonym : entity.getSynonyms())
				AddNodeWithStringOptimized(synonym, entity, true);
		}
	}

	protected void BuildTree(List<? extends IEntity> newEntities) {
		logger.debug("BuildTree()");

		// Build keyword tree and transition function
		if (this.root == null) {
			root = new FullTreeNode(null, ' ');
		}

		int processedEntityCount = 0;
		int processedStringCount = 0;

		logger.debug("# Entities to process = " + newEntities.size());

		// Each Entity is a unique combo of id,name,type
		// In the KnowledgeGraph only id,type are used
		// but in the dictionary a single Entity may have synonyms which point
		// to the same entitynode
		// ------------------------------------------------
		for (IEntity entity : newEntities) {
			if (++processedEntityCount % 10000 == 0) {
				logger.debug("num Entities processed so far = "
						+ processedEntityCount);
				logger.debug("num Entity Names processed so far = "
						+ processedStringCount);
				logger.debug("num ITreeNode instances so far = "
						+ TreeNodeBase.OBJECT_COUNT);
			}

			// Lets load this entity into the knowledge graph as well
			if (verbose && logger.isDebugEnabled()) {
				logger.debug("Entity = " + entity);
			}

			if (entity.getSynonyms() == null) {
				++processedStringCount;
				AddNodeWithStringOptimized(entity.getName(), entity, true);
			} else {
				for (String synonym : entity.getSynonyms()) {
					++processedStringCount;
					AddNodeWithStringOptimized(synonym, entity, true);
				}
			}
		}

		logger.debug("num Entities processed so far = " + processedEntityCount);
		logger.debug("num Entity Names processed so far = "
				+ processedStringCount);
		logger.debug("num ITreeNode instances so far = "
				+ TreeNodeBase.OBJECT_COUNT);
	}

	protected void AddNodeWithString(String entityName, IEntity entity,
			boolean updateTermHistogram) {
		String normalizedEntityName = AsciiUtils.convertNonAscii(entityName.trim());

		if (normalizedEntityName == null || normalizedEntityName.length() == 0) {
			return;
		}

		if (this.lowercase) {
			normalizedEntityName = normalizedEntityName.toLowerCase();
		}

		if (logger.isDebugEnabled())
			;
		{
			logger.debug("adding normalizedEntityName = "
					+ normalizedEntityName);
		}

		// add pattern to tree
		ITreeNode nd = root;
		char prevChar = ' ';
		char curChar;
		int lastSpaceIndex = 0;
		int curIndex = -1;

		for (int i = 0; i < normalizedEntityName.length(); ++i) {
			curChar = normalizedEntityName.charAt(i);
			curIndex++;

			// basically we skip all but the first instance of any whitespace
			// and regardless of what or how many whitespace characters we have,
			// we put a single space ' ' in the keyword tree.
			if (Character.isWhitespace(curChar)) {
				if (prevChar == ' ') {
					lastSpaceIndex = curIndex;
					continue;
				}

				// this is where we can deal with individual terms for term
				// frequency
				String term = normalizedEntityName.substring(lastSpaceIndex,
						curIndex);
				if (updateTermHistogram) {
					AddTerm(term);
				}
				lastSpaceIndex = curIndex + 1;

				curChar = ' ';
			}

			ITreeNode ndNew = null;
			if (nd.getTransitions() != null) {
				if (nd.containsTransition(curChar)) {
					ndNew = nd.getTransition(curChar);
				}
			}

			if (ndNew == null) {
				// We create new TreeNode derived instance
				// --------------------
				ndNew = new FullTreeNode(nd, curChar);

				// lets add the transition
				nd.addTransition(ndNew);
			}

			nd = ndNew;
			prevChar = curChar;

		} // end for(i=0, i < trimmed_name.length

		// Add final term
		String lastterm = normalizedEntityName.substring(lastSpaceIndex,
				curIndex + 1);
		if (updateTermHistogram) {
			AddTerm(lastterm);
		}

		if (verbose && logger.isDebugEnabled()) {
			logger.debug("add result = (entity = '" + entity + "')");
		}

		nd.addResult(new EntityResult(curIndex + 1, entity));
	}

	protected void AddNodeWithStringOptimized(String entityName,
			IEntity entity, boolean updateTermHistogram) {
		String normalizedEntityName = AsciiUtils.convertNonAscii(entityName
				.trim());

		if (normalizedEntityName == null || normalizedEntityName.length() == 0) {
			return;
		}

		if (this.lowercase) {
			normalizedEntityName = normalizedEntityName.toLowerCase();
		}

		if (logger.isDebugEnabled())
			;
		{
			logger.debug("adding normalizedEntityName = "
					+ normalizedEntityName);
		}

		// add pattern to tree
		ITreeNode nd = root;
		char prevChar = ' ';
		char curChar;
		int lastSpaceIndex = 0;
		int curIndex = -1;
		for (int i = 0; i < normalizedEntityName.length(); ++i) {
			curChar = normalizedEntityName.charAt(i);
			curIndex++;

			// basically we skip all but the first instance of any whitespace
			// and regardless of what or how many whitespace characters we have,
			// we put a single space ' ' in the keyword tree.
			if (Character.isWhitespace(curChar)) {
				if (prevChar == ' ') {
					lastSpaceIndex = curIndex;
					continue;
				}

				// TODO: whitespace usually marks the delimeter between terms
				// this is where we can deal with individual terms for term
				// frequency
				String term = normalizedEntityName.substring(lastSpaceIndex,
						curIndex);
				if (updateTermHistogram) {
					AddTerm(term);
				}
				lastSpaceIndex = curIndex + 1;

				curChar = ' ';
			}

			ITreeNode ndNext = null;
			if (nd.getTransitions() != null) {
				if (nd.containsTransition(curChar)) {
					ndNext = nd.getTransition(curChar);
				}
			}

			if (ndNext == null) {
				// IF we need to replace the parent Node of ndNext because we're
				// adding a 2nd transition
				// then we need to update the parentNode of the current node
				// (ndPrev is parent of nd)
				// So that its transition goes to the new node
				// ------------------------------------------
				ITreeNode nodePtrToAddTransition = nd
						.getTreeNodeForAddingTransition();
				if (nodePtrToAddTransition != nd) {
					// We've switched the underlying TreeNode instance, we need
					// to update the previous node's parent
					// to point to new instance
					// ----------------------
					nodePtrToAddTransition.getParent().addTransition(
							nodePtrToAddTransition);

					// we need to update the parent node of any transitions that
					// extisted for the
					Collection<ITreeNode> transitions = nodePtrToAddTransition
							.getTransitions();
					if (transitions != null) {
						for (ITreeNode treeNode : transitions) {
							treeNode.setParent(nodePtrToAddTransition);
						}
					}
				}

				// We create new TreeNode derived instance
				// we start with single result & single transition to try and
				// conserve space
				// If we need to add multiple results or multiple transitions
				// later, we will update the
				// type of node instance in-place of the trie
				// --------------------
				ndNext = new Result1Transition1TreeNode(nodePtrToAddTransition,
						curChar);

				// lets add the transition
				nodePtrToAddTransition.addTransition(ndNext);
			}

			nd = ndNext;
			prevChar = curChar;

		} // end for(i=0, i < trimmed_name.length

		// Add final term
		String lastterm = normalizedEntityName.substring(lastSpaceIndex,
				curIndex + 1);
		if (updateTermHistogram) {
			AddTerm(lastterm);
		}

		if (verbose && logger.isDebugEnabled()) {
			logger.debug("add result = (entity = '" + entity + "')");
		}

		ITreeNode nodePtrToAddResult = nd.getTreeNodeForAddingResult();
		if (nodePtrToAddResult != nd) {
			// TODO:? We may need to walk the parent tree all the way to root if
			// we keep
			// modifygin the underlying result storage type (result1 -->
			// multiple)
			// ----------------------------------------------------------------
			nodePtrToAddResult.getParent().addTransition(nodePtrToAddResult);

			// we need to update the parent node of any transitions that existed
			// for the
			Collection<ITreeNode> transitions = nodePtrToAddResult
					.getTransitions();
			if (transitions != null) {
				for (ITreeNode treeNode : transitions) {
					treeNode.setParent(nodePtrToAddResult);
				}
			}
		}

		nodePtrToAddResult.addResult(new EntityResult(curIndex + 1, entity));
	}

	protected void BuildFailureLinks() {
		logger.debug("BuildFailureLinks()");

		// Find failure functions
		List<ITreeNode> nodes = new ArrayList<ITreeNode>();

		// level 1 nodes - fail to root node
		for (ITreeNode nd : root.getTransitions()) {
			nd.setFailure(root);
			if (nd.getTransitions() != null) {
				for (ITreeNode trans : nd.getTransitions()) {
					nodes.add(trans);
				}
			}
		}

		// other nodes - using BFS
		while (nodes.size() != 0) {
			List<ITreeNode> newNodes = new ArrayList<ITreeNode>();
			for (ITreeNode nd : nodes) {
				ITreeNode r = nd.getParent().getFailure();
				Character c = nd.getChar();

				while (r != null && !r.containsTransition(c)) {
					r = r.getFailure();
				}

				if (r == null) {
					nd.setFailure(root);
				} else {
					nd.setFailure(r.getTransition(c));
					if (nd.getFailure().getResults() != null) {
						for (EntityResult result : nd.getFailure().getResults()) {
							nd.addResult(result);
						}
					}
				}

				// add child nodes to BFS list
				if (nd.getTransitions() != null) {
					for (ITreeNode child : nd.getTransitions()) {
						newNodes.add(child);
					}
				}
			}
			nodes = newNodes;
		}
		root.setFailure(root);
	}

	protected void BuildFailureLinksOptimized() {
		logger.debug("BuildFailureLinksOptimized()");

		// Find failure functions
		List<ITreeNode> nodes = new ArrayList<ITreeNode>();

		// level 1 nodes - fail to root node
		for (ITreeNode nd : root.getTransitions()) {
			nd.setFailure(root);
			if (nd.getTransitions() != null) {
				for (ITreeNode trans : nd.getTransitions()) {
					nodes.add(trans);
				}
			}
		}

		// other nodes - using BFS
		while (nodes.size() != 0) {
			List<ITreeNode> newNodes = new ArrayList<ITreeNode>();
			for (ITreeNode nd : nodes) {
				ITreeNode r = nd.getParent().getFailure();
				Character c = nd.getChar();

				while (r != null && !r.containsTransition(c)) {
					r = r.getFailure();
				}

				if (r == null) {
					nd.setFailure(root);
				} else {
					nd.setFailure(r.getTransition(c));
					if (nd.getFailure().getResults() != null) {
						// We need to copy results from failure node to this
						// node
						// because the failure node is "contained" within this
						// node
						// ------------------------------------------------------

						for (EntityResult result : nd.getFailure().getResults()) {
							// Each time we add a result, we may need to switch
							// the underlying treenode instance
							// --------------------------------------------------------------------------------
							ITreeNode nodePtrToAddResult = nd
									.getTreeNodeForAddingResult();
							if (nodePtrToAddResult != nd) {
								nodePtrToAddResult.setFailure(nd.getFailure());

								// Need to update parent of the pre-existing
								// node to point to the newly created
								// node
								// ----------------------------------------------------------------
								nodePtrToAddResult.getParent().addTransition(
										nodePtrToAddResult);

								// we need to update the parent node of any
								// transitions that existed for the
								// pre-existing node
								// --------------------
								Collection<ITreeNode> transitions = nodePtrToAddResult
										.getTransitions();
								if (transitions != null) {
									for (ITreeNode treeNode : transitions) {
										treeNode.setParent(nodePtrToAddResult);
									}
								}
								nd = nodePtrToAddResult;
							}
							nd.addResult(result);
						}
					}
				}

				// add child nodes to BFS list
				if (nd.getTransitions() != null) {
					for (ITreeNode child : nd.getTransitions()) {
						newNodes.add(child);
					}
				}
			}
			nodes = newNodes;
		}
		root.setFailure(root);
	}

	protected boolean isTokenSeparateWord(StringSearchResult potentialMatch,
			String text) {
		// TODO: make this configurable to find matches in twitter hashtags and
		// such
		int indexstart = potentialMatch.getStart();
		int indexend = potentialMatch.getEnd();

		if (indexstart > 0) {
			char before = text.charAt(indexstart - 1);
			if (Character.isLetterOrDigit(before)) {
				// if(logger.isDebugEnabled())
				// {
				// logger.debug("isTokenSeparateWord() check for '" +
				// potentialMatch.getKeyword() + "' failed due to prev char = '"
				// + before + "'");
				// }
				return false;
			}
		}

		if (indexend < text.length()) {
			char after = text.charAt(indexend);
			if (Character.isLetterOrDigit(after)) {
				// if(logger.isDebugEnabled())
				// {
				// logger.debug("isTokenSeparateWord() check for '" +
				// potentialMatch.getKeyword() +
				// "' failed due to after char = '" + after + "'");
				// }
				return false;
			}
		}

		return true;
	}

	protected void AddTerm(String term) {
		int count = termHistogram.get(term);
		termHistogram.put(term, ++count);

		if (verbose && logger.isDebugEnabled()) {
			logger.debug("AddTerm(term = '" + term + "', count = " + count
					+ ")");
		}

		++totalTermCount;
	}

	/**
	 * @return the lowercase
	 */
	public boolean isLowercase() {
		return lowercase;
	}

	/**
	 * @param lowercase
	 *            the lowercase to set
	 */
	public void setLowercase(boolean lowercase) {
		this.lowercase = lowercase;
	}

	/**
	 * @return the termHistogram
	 */
	public TObjectIntHashMap<String> getTermHistogram() {
		return termHistogram;
	}

	/**
	 * @return the totalTermCount
	 */
	public int getTotalTermCount() {
		return totalTermCount;
	}
}
