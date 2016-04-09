import core.SearchCore;

public class Main {
	public static void main(String[] args) {
		SearchCore core = new SearchCore();
		while(true) {
			System.out.println("*****************");
			core.search();
			try {Thread.sleep(15000);}catch(Exception e) {}
		}
	}
}
