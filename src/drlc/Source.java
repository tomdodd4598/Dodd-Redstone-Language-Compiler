package drlc;

import drlc.node.Node;

public class Source {
	
	public final String fileName;
	public final String contents;
	public final Node[] parseNodes;
	
	public final String dirName;
	public final String fileExtension;
	
	public Source(String fileName, String contents, Node... parseNodes) {
		this.fileName = fileName.replace('\\', '/');
		this.contents = contents;
		this.parseNodes = parseNodes;
		
		int index = this.fileName.lastIndexOf('.');
		this.dirName = index < 0 ? this.fileName : this.fileName.substring(0, index);
		this.fileExtension = index < 0 ? "" : this.fileName.substring(index);
	}
	
	public String getSubModuleFileName(String moduleName) {
		if (fileName.equals(Main.rootFile)) {
			int index = dirName.lastIndexOf('/');
			return index < 0 ? moduleName + fileExtension : dirName.substring(0, index) + "/" + moduleName + fileExtension;
		}
		else {
			return dirName + "/" + moduleName + fileExtension;
		}
	}
}
