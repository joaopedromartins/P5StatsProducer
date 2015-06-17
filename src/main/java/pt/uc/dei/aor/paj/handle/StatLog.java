package pt.uc.dei.aor.paj.handle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StatLog {
	
	private static final String logname = "statistics.log";
	
	public void adicionarLinha(String texto){
		
		try {
			
			File file = new File(logname);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append(texto + "\n");
			bw.close();
			
		} catch (IOException e) {
			System.out.println("Erro ao escrever no ficheiro "+ logname);
		}
	}
	


}
