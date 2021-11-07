package p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
//Java는 eclipse에서 JDK11, C 파일은 ubuntu 환경에서 gcc 컴파일러 명령을 통해 테스트하였습니다.
public class HW1 {
	File file;
	Reader reader;
	BufferedReader br;
	FileOutputStream fos;
	PrintStream ps;
	
	void Set_stream(HW1 pt) throws Exception { //입출력 스트림을 설정합니다.
		pt.file = new File("C:\\Users\\qhstj\\Desktop\\test.hf");
		pt.reader = new FileReader(pt.file);
		pt.br = new BufferedReader(pt.reader);
		
		pt.fos = new FileOutputStream("C:\\\\Users\\\\qhstj\\\\Desktop\\\\test.c");
		pt.ps = new PrintStream(pt.fos);
		
	}
	void Start(HW1 p) {
		// c 파일의 도입부 내용을 추가합니다
		p.ps.print("#include <stdio.h>\n#include <stdlib.h>\nint main(){\n");
	}
	void End(HW1 p) throws Exception {
	//c 파일의 마지막 부분을 추가해주고 스트림을 닫습니다.
		p.ps.print("\treturn 0;\n}");
		
		p.br.close();
		p.ps.flush();
		p.ps.close();
	}
	
	void Read_Write(HW1 pt) throws Exception {
		//줄 단위로 읽어들입니다.
		while(true) {
			String data = pt.br.readLine();
			if(data==null) break;
			
			String sub1 = data.substring(1, data.length()-1);
			String order1 = sub1.split(" ")[0];
			String order2 = sub1.substring(sub1.indexOf(" ")+1);

			
			switch(order1) {
			case "echo":
				//출력
				pt.ps.println("\tsystem(\"echo "+order2.substring(1, order2.length())+");");
				break;
			case "list_dir":
				//모든 파일을 보임
				pt.ps.println("\tsystem(\"ls -al\");");
				break;
			case "del":
				// 해당 파일 제거
				pt.ps.println("\tsystem(\"rm "+order2.substring(1, order2.length())+");");
				break;
			case "show":
				// cat 명령으로 파일 내용 출력
				pt.ps.println("\tsystem(\"cat "+order2.substring(1, order2.length())+");");
				break;
			case "mov":
				String o1 = sub1.split(" ")[1];
				String o2 = sub1.split(" ")[2];
				//ls -al > [파일이름] : 폴더 내의 파일의 리스트를 출력하고 그 결과를 텍스트 파일로 저장
				if (o1.equals("list_dir")) {
					pt.ps.println("\tsystem(\"ls -al > "+o2.substring(1, o2.length()-1)+".txt\");");	
				}
								
			}
			pt.ps.flush();
		}
	}
	
		
	public static void main(String[] args) throws Exception {
		HW1 pt = new HW1();
		pt.Set_stream(pt);
		pt.Start(pt);
		pt.Read_Write(pt);
		pt.End(pt);
	}

}
