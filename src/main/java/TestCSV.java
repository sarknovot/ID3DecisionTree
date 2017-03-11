import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class TestCSV {

	public static void main(String[] args)throws FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		FileReader fr = new FileReader("zkouska.csv");
		PreprocessCSV prCSV = new PreprocessCSV(fr);
		
	}

}
