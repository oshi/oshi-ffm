package ooo.oshi.util.platform.windows;

public class Win32Exception extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public Win32Exception(int code)	{
		this(Integer.toString(code));
	}
	
	public Win32Exception(String codeString)	{
		super("GetLastError() returned " + codeString);
	}

}
