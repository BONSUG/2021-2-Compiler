package p1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;
//Java�� eclipse���� JDK11, C ������ ubuntu ȯ�濡�� gcc �����Ϸ� ����� ���� �׽�Ʈ�Ͽ����ϴ�.
public class HW1 {
	File file;
	Reader reader;
	BufferedReader br;
	FileOutputStream fos;
	PrintStream ps;
	
	void Set_stream(HW1 pt) throws Exception { //����� ��Ʈ���� �����մϴ�.
		pt.file = new File("C:\\Users\\qhstj\\Desktop\\test.hf");
		pt.reader = new FileReader(pt.file);
		pt.br = new BufferedReader(pt.reader);
		
		pt.fos = new FileOutputStream("C:\\\\Users\\\\qhstj\\\\Desktop\\\\test.c");
		pt.ps = new PrintStream(pt.fos);
		
	}
	void Start(HW1 p) {
		// c ������ ���Ժ� ������ �߰��մϴ�
		p.ps.print("#include <stdio.h>\n#include <stdlib.h>\nint main(){\n");
	}
	void End(HW1 p) throws Exception {
	//c ������ ������ �κ��� �߰����ְ� ��Ʈ���� �ݽ��ϴ�.
		p.ps.print("\treturn 0;\n}");
		
		p.br.close();
		p.ps.flush();
		p.ps.close();
	}
	
	void Read_Write(HW1 pt) throws Exception {
		//�� ������ �о���Դϴ�.
		while(true) {
			String data = pt.br.readLine();
			if(data==null) break;
			
			String sub1 = data.substring(1, data.length()-1);
			String order1 = sub1.split(" ")[0];
			String order2 = sub1.substring(sub1.indexOf(" ")+1);

			
			switch(order1) {
			case "echo":
				//���
				pt.ps.println("\tsystem(\"echo "+order2.substring(1, order2.length())+");");
				break;
			case "list_dir":
				//��� ������ ����
				pt.ps.println("\tsystem(\"ls -al\");");
				break;
			case "del":
				// �ش� ���� ����
				pt.ps.println("\tsystem(\"rm "+order2.substring(1, order2.length())+");");
				break;
			case "show":
				// cat ������� ���� ���� ���
				pt.ps.println("\tsystem(\"cat "+order2.substring(1, order2.length())+");");
				break;
			case "mov":
				String o1 = sub1.split(" ")[1];
				String o2 = sub1.split(" ")[2];
				//ls -al > [�����̸�] : ���� ���� ������ ����Ʈ�� ����ϰ� �� ����� �ؽ�Ʈ ���Ϸ� ����
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
