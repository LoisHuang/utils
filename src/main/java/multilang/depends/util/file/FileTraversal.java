/*
MIT License

Copyright (c) 2018-2019 Gang ZHANG

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package multilang.depends.util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Recursively visit every file in the given root path using the 
 * extended IFileVisitor
 *
 */
public class FileTraversal {
	/**
	 * The visitor interface 
	 * Detail operation should be implemented here
	 */
	public interface IFileVisitor {
		void visit(File file);
	}
	
	IFileVisitor visitor;
	private ArrayList<String> extensionFilters = new ArrayList<>();
	private List<String> excludePaths = new ArrayList<>();
	boolean shouldVisitDirectory = false;
	boolean shouldVisitFile = true;
	public FileTraversal(IFileVisitor visitor){
		this.visitor = visitor;
	}

	public FileTraversal(IFileVisitor visitor,boolean shouldVisitDirectory,boolean shouldVisitFile){
		this.visitor = visitor;
		this.shouldVisitDirectory = shouldVisitDirectory;
		this.shouldVisitFile = shouldVisitFile;
	}
	
	public void travers(String path) {
		File dir = new File(path);
		travers(dir);
	}

	public void travers(File root) {
		File[] files = root.listFiles();
		if (files == null)
			return;
		for (int i = 0; i < files.length; i++) {
			if (isExclude(files[i])) {
				continue;
			}
			if (files[i].isDirectory()) {
				travers(files[i]);
				if (shouldVisitDirectory) {
					invokeVisitor(files[i]);
				}
			} else {
				if (shouldVisitFile) {
					invokeVisitor( files[i]);
				}
			}
		}		
	}

	private void invokeVisitor(File f) {
		if (extensionFilters.size()==0) {
			visitor.visit(f);
		}else {
			for (String ext:extensionFilters) {
				if (f.getAbsolutePath().toLowerCase().endsWith(ext.toLowerCase())) {
					visitor.visit(f);
				}
			}
		}
	}

	public FileTraversal extensionFilter(String ext) {
		this.extensionFilters.add(ext.toLowerCase());
		return this;
	}

	public void extensionFilter(String[] fileSuffixes) {
		for (String fileSuffix:fileSuffixes){
			extensionFilter(fileSuffix);
		}
	}
	public void setExcludePaths(List<String> excludePaths) {
		this.excludePaths = excludePaths;
	}

	public boolean isExclude(File file) {
		if (excludePaths.size() == 0) return false;
		for (String exc : excludePaths) {
			try {
				if (file.getCanonicalPath().startsWith(FileUtil.uniqFilePath(exc))) {
					return true;
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}