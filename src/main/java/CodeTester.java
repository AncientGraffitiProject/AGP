
public class CodeTester {
	public static void main(String args[]) {
		String primarydo = "VI.17.36";
		
		String[] parts = primarydo.split("\\.");
		
		String pt1 = parts[0];
		String pt2 = parts[1];
		String pt3 = parts[2];
		
		String insulaName = pt1 + "." + pt2;
		String propNum = pt3;
		
		System.out.println("Insula Name: " + insulaName);
		System.out.println("Property Num: " + propNum);
	}
}
