package multilang.depends.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemporaryFile {
	Path tempDirWithPrefix;
	private static Map<Long, TemporaryFile> _insts = new ConcurrentHashMap<>();
	public static void reset() {
		for(TemporaryFile _inst : _insts.values()) {
			_inst.delete();
		}
		_insts.clear();
	}

	public static void resetCurrentThread() {
		long currentThreadId = Thread.currentThread().getId();
		TemporaryFile _inst = _insts.get(currentThreadId);
		if(_inst != null) {
			_inst.delete();
			_insts.remove(currentThreadId);
		}
	}

	public static TemporaryFile getInstance() {
		long currentThreadId = Thread.currentThread().getId();
		TemporaryFile _inst = _insts.get(currentThreadId);
		if(_inst == null) {
			_inst = new TemporaryFile(currentThreadId);
			_insts.put(currentThreadId, _inst);
		}
		return _inst;
	}

	private TemporaryFile(long threadId) {
		try {
			tempDirWithPrefix = Files.createTempDirectory("depends_" + threadId + ".tmp");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String exprPath(Integer id) {
		return tempDirWithPrefix.toAbsolutePath().toFile() + File.separator + id + ".expr";
	}
	
	public String macroPath(Integer fileId) {
		return tempDirWithPrefix.toAbsolutePath().toFile() + File.separator + fileId + ".macros";
	}
	
	public void delete() {
		if (tempDirWithPrefix==null) return;
		FileTraversal.IFileVisitor visitor = new FileTraversal.IFileVisitor() {
			@Override
			public void visit(File file) {
				try {
					Files.deleteIfExists(file.toPath());
				} catch (IOException e) {
				}
			}
		};
		FileTraversal t = new FileTraversal(visitor,true,true);
		t.travers(tempDirWithPrefix.toAbsolutePath().toFile());
		try {
			Files.deleteIfExists(tempDirWithPrefix);
		} catch (IOException e) {
		}
	}

}
