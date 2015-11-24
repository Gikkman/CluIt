package testing._helpers;

import com.cluit.util.dataTypes.Entry;

public class tsHelp {
	public static String atos(double[] d){
		String s = "[";
		for(int i = 0; i < d.length; i++)
			s += d[i] + ", ";
		s = s.substring(0, s.length()-2);
		s+="]";
		return s;
	}

	public static String atos(int[] d) {
		String s = "[";
		for(int i = 0; i < d.length; i++)
			s += d[i] + ", ";
		s = s.substring(0, s.length()-2);
		s+="]";
		return s;
	}

	public static String atos(Entry e) {
		String s = "[";
		for(int i = 0; i < e.getDimensions(); i++)
			s += e.getCoordinateAt(i) + ", ";
		s = s.substring(0, s.length()-2);
		s+="]";
		return s;
	}
	
	public static <T> boolean compareArray(T[] a1, T[] a2){
		if(a1 == null || a2 == null)
			return false;
		if(a1.length != a2.length)
			return false;
		for(int i = 0; i < a1.length; i++){
			if( !a1[i].equals(a2[i]) )
				return false;
		}
		return true;
	}

	public static boolean ArraysMatch(int[] a1, int[] a2) {
		if(a1 == null || a2 == null)
			return false;
		if(a1.length != a2.length)
			return false;
		for(int i = 0; i < a1.length; i++){
			if( a1[i] != a2[i] )
				return false;
		}
		return true;
	}
	
	public static boolean ArraysMatch(double[] a1, double[] a2) {
		if(a1 == null || a2 == null)
			return false;
		if(a1.length != a2.length)
			return false;
		for(int i = 0; i < a1.length; i++){
			if( a1[i] != a2[i] )
				return false;
		}
		return true;
	}
}
