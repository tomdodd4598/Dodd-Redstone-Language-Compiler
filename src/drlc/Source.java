package drlc;

import drlc.node.Node;

public class Source {
	
	public final String fileName;
	public final String contents;
	public final Node[] parseNodes;
	
	public Source(String fileName, String contents, Node... parseNodes) {
		this.fileName = fileName.replace('\\', '/');
		this.contents = contents;
		this.parseNodes = parseNodes;
	}
}
