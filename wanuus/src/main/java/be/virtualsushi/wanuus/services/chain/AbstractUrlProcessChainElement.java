package be.virtualsushi.wanuus.services.chain;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class AbstractUrlProcessChainElement extends AbstractProcessChainElement<Document> {

	private Elements foundElements;

	@Override
	protected boolean canProcess(Document object) {
		foundElements = object.select(getSelector());
		return !foundElements.isEmpty();
	}

	protected Elements getFoundElements() {
		return foundElements;
	}

	protected abstract String getSelector();

}
