package testing._main;

import java.io.IOException;

import com.cluit.util.Const;
import com.cluit.util.AoP.Invocation;
import com.cluit.util.AoP.MethodMapper;

import testing.clustering.Test_Cluster;
import testing.clustering.Test_Helper;
import testing.clustering.Test_Space;
import testing.io.Test_ExcelReader;
import testing.io.Test_Image;
import testing.utils.Test_MethodMapper;
import testing.utils.Test_MultiMethodMapper;
import testing.utils.Test_PriorityQueues;
import testing.utils.Test_ReferencePasser;

public class TestSuit {
	

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		if( false ){
			System.out.println("Enabling debug loggs. This will probably cause a log of \"syserr\"s :-D\n");
			MethodMapper.addMethod(Const.METHOD_EXCEPTION_API, new Ex() );
			MethodMapper.addMethod(Const.METHOD_EXCEPTION_GENERAL, new Ex() );
			MethodMapper.addMethod(Const.METHOD_EXCEPTION_JS, new Ex() );
		}
		
		Test_Helper.run(args);
		Test_PriorityQueues.run(args);
		Test_Image.run(args);
		Test_Cluster.run();
		Test_Space.run();
		Test_MethodMapper.run();
		Test_MultiMethodMapper.run();
		Test_ReferencePasser.run();
		Test_ExcelReader.run();
							
	}

	private static class Ex implements Invocation{
		@Override
		public void execute(Object... args) {
			System.err.println((String) args[0]);
		}		
	}
}
