import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.antlr.v4.runtime.tree.ParseTreeProperty;




public class MiniCPrintListener extends MiniCBaseListener 
{
	ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();
	
	boolean isBinaryOperation(MiniCParser.ExprContext ctx) {
		//항이 두개인 경우를 찾습니다.
		return ctx.getChildCount()==3 && ctx.getChild(1)!=ctx.expr()&& ctx.getChild(0)!=ctx.IDENT();
	}
	boolean isIdent(MiniCParser.ExprContext ctx) {
		// Ident 하나만 있는 경우
		return ctx.getChild(0)==ctx.IDENT()&&ctx.getChildCount()==1;
	}
	boolean isIdentPlus(MiniCParser.ExprContext ctx) {
		//	IDENT '[' expr ']', IDENT '(' args ')'의 경우
		return ctx.getChild(0)==ctx.IDENT()&&ctx.getChildCount()==4;
	}
	boolean isIdentPlus2(MiniCParser.ExprContext ctx) {
		//IDENT '=' expr의 경우
		return ctx.getChild(0)==ctx.IDENT()&&ctx.getChildCount()==3;
	}
	boolean isIdentPlus3(MiniCParser.ExprContext ctx) {
		//IDENT '[' expr ']' '=' expr의 경우
		return ctx.getChild(0)==ctx.IDENT()&&ctx.getChildCount()==6;
	}
	boolean isUnaryOp(MiniCParser.ExprContext ctx) {
		//단항 연산자의 경우
		return ctx.getChild(0)!=ctx.expr()&&ctx.getChildCount()==2;
	}
	boolean isNested(MiniCParser.ExprContext ctx) {
		//괄호로 묶인 경우  '(' expr ')'	
		return ctx.getChild(0).getText()=="("&&ctx.getChild(2)!=ctx.expr()&&ctx.getChildCount()==3;
	}
	boolean isLiteral(MiniCParser.ExprContext ctx) {
		//LITERAL인 경우
		return ctx.getChild(0)==ctx.LITERAL()&&ctx.getChildCount()==1;
	}
	@Override
	public void exitProgram(MiniCParser.ProgramContext ctx) {

        String program = "";

        for (int i = 0; i < ctx.getChildCount(); i++) {
            newTexts.put(ctx, ctx.decl(i).getText()); //ParseTree인 newText에 decl을 넣음
            program += newTexts.get(ctx.getChild(i)); //ctx의 child에 들어갔다가 나오면서 출력
        }

        System.out.println(program);
        File file = new File(String.format("[HW3]201904237.c")); // 본인 학번으로 변경해주세요.

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(program);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
		
	
	@Override
	public void exitDecl(MiniCParser.DeclContext ctx){
		//Child의 첫 요소가 var_decl나 fun_decl인 경우 적절히 호출할 수 있도록 조건문을 생성합니다.
		if(ctx.getChild(0)==ctx.var_decl()) {
			newTexts.put(ctx, newTexts.get(ctx.var_decl()));
		}
		else {
			newTexts.put(ctx, newTexts.get(ctx.fun_decl()));
			}
		
	}
	@Override
	public void exitVar_decl(MiniCParser.Var_declContext ctx) {
		//type_spec IDENT ';' 
		//| type_spec IDENT '=' LITERAL ';'	
		//| type_spec IDENT '[' LITERAL ']' ';'	;
		//Child의 수 만큼 위 세가지 중 하나를 선택해 호출합니다.
		int cnt=ctx.getChildCount();
		String t = ctx.type_spec().getText();
		String i = ctx.IDENT().getText();
		switch(cnt) {
		case 3:
			newTexts.put(ctx,t+" "+i+"\n");
			break;
		case 5:
			String l = ctx.LITERAL().getText();
			newTexts.put(ctx,t+" "+i+" = "+l+";\n");
			break;
		case 6:
			String l1 = ctx.LITERAL().getText();
			newTexts.put(ctx,t+" "+i+"["+l1+"]"+";\n");
			break;
		}
	}
	@Override
	public void exitFun_decl(MiniCParser.Fun_declContext ctx) {
		//type_spec IDENT '(' params ')' compound_stmt ;
		//params와 compound_stmt는 자식 노드에서 가져오고 나머지는 텍스트로 불러옵니다.
		String t,i,p,c;
		t=newTexts.get(ctx.type_spec());
		i=ctx.IDENT().getText();
		p=newTexts.get(ctx.params());
		c=newTexts.get(ctx.compound_stmt());
		newTexts.put(ctx,(t+i+"("+p+")"+c));
	}
	@Override
	public void exitType_spec(MiniCParser.Type_specContext ctx) {
		//타입(void int) 두 가지 중 하나를 put합니다.
		String type=ctx.getChild(0).getText();
		newTexts.put(ctx,type+" ");
	}
	@Override
	public void exitParams(MiniCParser.ParamsContext ctx) {
		//파라미터 젠체이므로 개수를 파악하고 자식 노드에서 결과를 불러옵니다. 
		int cnt=ctx.getChildCount();
		if(cnt==0) newTexts.put(ctx, ""); //파라미터가 없는 경우
		else {
			if(cnt==1) { //한 가지인 경우
				String s="";
				s=newTexts.get(ctx.param(0));
				newTexts.put(ctx, s);
			}
			else { //두개 이상인 경우
				String s="";
				for(int i=0;i<cnt-1;i++) {
					String x=newTexts.get(ctx.param(i)); //param의 i 번재 요소를 가져옵니다.
					if(x==null) {
						break;
						}
					else s+=x+", ";
				}
				s=s.substring(0, s.length()-2);//마지막 요소에서 ,를 빼줍니다.
				newTexts.put(ctx, s);
			}
		}
	}
	@Override
	public void exitParam(MiniCParser.ParamContext ctx) {
		//type_spec IDENT		
		//| type_spec IDENT '[' ']'	;
		//child의 개수가 3 이상이면 배열 형태로 아닌 경우 변수로 put합니다. 
		int cnt=ctx.getChildCount();
		String t1 = newTexts.get(ctx.type_spec());
		String i1 = ctx.IDENT().getText();
		if(cnt>2) newTexts.put(ctx, t1+" "+i1+"["+"]");
		else newTexts.put(ctx, (t1+" "+i1));
	}
	
	@Override
	public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) {
		//'{' local_decl* stmt* '}'
		//* 표시가 있으므로 null이 아닐때까지 각각 호출하여 결과를 얻어옵니다.
		int cnt=ctx.getChildCount();
		String x1="",x2="";
		for(int i =0;i<cnt-1;i++) {
			String s = newTexts.get(ctx.local_decl(i));
			if(s==null) continue;
			else x1+="...."+s+"\n";
		}
		for(int i =0;i<cnt-1;i++) {
			String s = newTexts.get(ctx.stmt(i));
			if(s==null) continue;
			else x2+="...."+s+"\n";
		}
		newTexts.put(ctx,"\n{\n"+x1+x2+"}");
	}
	@Override
	public void exitLocal_decl(MiniCParser.Local_declContext ctx) {
		//type_spec IDENT ';'
		//| type_spec IDENT '=' LITERAL ';'	
		//| type_spec IDENT '[' LITERAL ']' ';'	;
		//Child의 개수에 따라 적절히 위 세 가지 중 하나를 put합니다.
		int cnt=ctx.getChildCount();
		
		String t =newTexts.get(ctx.type_spec());
		String i = ctx.IDENT().getText();
		if(cnt==3) newTexts.put(ctx,t+" "+i+";");
		else {
			String l = ctx.LITERAL().getText();
			if(ctx.getChild(2).getText().equals("="))
				newTexts.put(ctx,t+" "+i+" = "+l+";");
			else newTexts.put(ctx,t+" "+i+"["+l+"];");
		}
	}
	
	@Override
	public void exitStmt(MiniCParser.StmtContext ctx) {
		// expr_stmt	 compound_stmt   if_stmt  while_stmt return_stmt
		// 첫 요소가 위 네가지 중 무엇을 가리키는지 확인해 자식 노드로 부터 불러옵니다.
		String s1="";
		if(ctx.getChild(0)==ctx.expr_stmt())  s1=newTexts.get(ctx.expr_stmt());
		else if(ctx.getChild(0)==ctx.compound_stmt())  s1=newTexts.get(ctx.compound_stmt());
		else if(ctx.getChild(0)==ctx.if_stmt())  s1=newTexts.get(ctx.if_stmt());
		else if(ctx.getChild(0)==ctx.while_stmt())  s1=newTexts.get(ctx.while_stmt());
		else if(ctx.getChild(0)==ctx.return_stmt())  s1=newTexts.get(ctx.return_stmt());
		newTexts.put(ctx,s1);
	}
	@Override
	public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) {
		//expr ';' 형태이므로 expr의 결과를 가져옵니다
		String s1= newTexts.get(ctx.expr());
		newTexts.put(ctx,s1);
	}
	@Override
	public void exitExpr(MiniCParser.ExprContext ctx) {
		//위에서 설정한 조건들로 expr에 형테에 맞게 put합니다
		String s1="",s2="",op="";
		if(isBinaryOperation(ctx)) {//이항연산
			s1=newTexts.get(ctx.expr(0));
			s2=newTexts.get(ctx.expr(1));
			op=ctx.getChild(1).getText();
			newTexts.put(ctx,s1+" "+op+" "+s2);
		}
		else if(isIdent(ctx)) {
			//ident 하나만 있는 경우
			String id=ctx.IDENT().getText();
			newTexts.put(ctx,id);
		}
		else if(isIdentPlus(ctx)) {
			//IDENT '[' expr ']', IDENT '(' args ')'의 경우
			int a=ctx.getChildCount();
			String id=ctx.IDENT().getText();
			String x1=newTexts.get(ctx.expr(0));
			if(ctx.getChild(1).equals("[")) 
				newTexts.put(ctx,id+" ["+x1+"];");
			else {
				String args = newTexts.get(ctx.args());
				newTexts.put(ctx,id+"("+args+");");
			}
		}
		else if (isUnaryOp(ctx)) {
			//단항연산의 경우
			String op1 = ctx.getChild(0).getText();
			String x1=newTexts.get(ctx.expr(0));
			newTexts.put(ctx,op1+x1+";");
		}
		else if (isNested(ctx)) {
			//(expr)의 경우
			String x1=newTexts.get(ctx.expr(0));
			newTexts.put(ctx,"("+x1+");");
		}
		else if (isLiteral(ctx)) {
			//Literal의 경우
			String x1=ctx.LITERAL().getText();
			newTexts.put(ctx,x1);
		}
		else if (isIdentPlus2(ctx)) {
			//IDENT '=' expr의 경우
			String id=ctx.IDENT().getText();
			String x1=newTexts.get(ctx.expr(0));
			newTexts.put(ctx,id+" = "+x1+";");
		}
		else if (isIdentPlus3(ctx)) {
			//IDENT '[' expr ']' '=' expr의 경우
			String id=ctx.IDENT().getText();
			String x1=newTexts.get(ctx.expr(0));
			String x2=newTexts.get(ctx.expr(1));
			newTexts.put(ctx,id+"["+x1+"] = "+x2+";");
		}
	}	
	@Override
	public void exitArgs(MiniCParser.ArgsContext ctx) {
		//expr (',' expr)*	의 경우
		int cnt=ctx.getChildCount();
		if(cnt==0) newTexts.put(ctx, "");// 없는 경우
		else {
			if(cnt==1) {//하나일 때
				String x1=newTexts.get(ctx.expr(0));
				newTexts.put(ctx, x1);
			}
			else {//두개 이상인 경우
				String s="";
				for(int i=0;i<cnt-1;i++) {
					String x=newTexts.get(ctx.expr(i));
					if(x==null) {
						break;
						}
					else s+=x+", ";
				}
				s=s.substring(0, s.length()-2);//마지막 요소에서 ,제거
				newTexts.put(ctx, s);
			}
		}
	}

		@Override
	public void exitIf_stmt(MiniCParser.If_stmtContext ctx) {
		
		//  IF '(' expr ')' stmt	
		
		//| IF '(' expr ')' stmt ELSE stmt

		int cnt = ctx.getChildCount();
		String f= ctx.IF().getText();
		String ex = newTexts.get(ctx.expr());
		ex=ex.substring(0, ex.length());
		String s1 = newTexts.get(ctx.stmt(0));
		if(s1.charAt(0)!='{'&&s1.charAt(0)!='\n') {
			s1="\n........"+s1;
		}
		//s1이 {로 시작하지 않는 경우 다음 라인에서 한칸 더 들여쓰도록 함
		else {
			//s1이 {로 시작하는 경우 \n기준으로 split하고 .... 붙임
			String[] arr =s1.split("\n");
			String s11="";
					//새로운 문자열을 만들어 ....을 필요한 위치에 배치하고 배열의 모든 요소를 이 문자열에 대입합니다. 
			for(int j=0;j<arr.length;j++) {
				if(j==0) arr[j]="\n";
				else if(j==1) arr[j]="...."+arr[j]+"\n";
				else if(j==arr.length-1) arr[j]="...."+arr[j]; 
				else {
					arr[j]="...."+arr[j]+"\n";
					}
						
				s11=s11+arr[j];
				s1=s11;//새로운 문자열의 내용을 기존 문자열에 대입합니다.
			}
		}
					
		if(cnt==5) {//if 문
			newTexts.put(ctx, f+" ("+ex+")"+s1);
		}
		else {
			String e= ctx.ELSE().getText(); // if else 문
			String s2 = newTexts.get(ctx.stmt(1));
			String[] arr1 =s2.split("\n");
			String s22="";
			//새로운 문자열을 만들어 ....을 필요한 위치에 배치하고 배열의 모든 요소를 이 문자열에 대입합니다. 
			for(int j=0;j<arr1.length;j++) {
				if(j==0) arr1[j]="\n";
				else if(j==1) arr1[j]="...."+arr1[j]+"\n";
				else if(j==arr1.length-1) arr1[j]="...."+arr1[j]; 
				else {
					
					arr1[j]="...."+arr1[j]+"\n";
					}
				
				s22=s22+arr1[j];
			}
			s2=s22;//새로운 문자열의 내용을 기존 문자열에 대입합니다
			newTexts.put(ctx, f+" ("+ex+")"+s1+"\n...."+e+s2);
		}
	}
	@Override
	public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) {
		//WHILE '(' expr ')' stmt
		String w= ctx.WHILE().getText();
		String ex = newTexts.get(ctx.expr());
		ex=ex.substring(0, ex.length());
		String s1 = newTexts.get(ctx.stmt());
		String[] arr =s1.split("\n");
		String s11="";
		//if문과 마찬가지로 split후 적절히 배치해 다시 합치고 기존 문자열에 대입합니다.
		for(int j=0;j<arr.length;j++) {
			if(j==0) arr[j]="\n";
			else if(j==1) arr[j]="...."+arr[j]+"\n";
			else if(j==arr.length-1) arr[j]="...."+arr[j]; 
			else {
				
				arr[j]="...."+arr[j]+"\n";
				}
			
			s11=s11+arr[j];
		}
		s1=s11;
		newTexts.put(ctx, w+" ("+ex+")"+s1);
	
	}
	@Override
	public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) {
		//RETURN ';'			
		//| RETURN expr ';'
		//리턴문에 expr 존재 여부를 cnt로 확인해 적절히 put합니다.
		String r = ctx.RETURN().getText();
		if(ctx.getChildCount()>2) {
			String ex = newTexts.get(ctx.expr());
			newTexts.put(ctx, r+" "+ex+";");
		}
		else newTexts.put(ctx, r+";");
	}
}

