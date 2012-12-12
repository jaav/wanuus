package be.virtualsushi.wanuus.services.chain;

import org.jsoup.nodes.Document;

public class ProcessUrlChainOpenTagElement extends AbstractUrlProcessChainElement {

	@Override
	protected String doProcess(Document object) {
		return getFoundElements().get(0).attr("content");
	}

	@Override
	protected String getSelector() {
		return "meta[property=og:image]";
	}

}
