import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class MiniGoPrintListener extends MiniGoBaseListener {

	ParseTreeProperty<String> newTexts = new ParseTreeProperty<String>();

	@Override
	public void exitProgram(MiniGoParser.ProgramContext ctx) {
		int i = 0;

		newTexts.put(ctx, ctx.decl(0).getText());

		while (ctx.decl(i) != null) {
			System.out.println(newTexts.get(ctx.decl(i)));
			i++;
		}
	}

	@Override
	public void exitDecl(MiniGoParser.DeclContext ctx) {
		if (ctx.getChild(0) instanceof MiniGoParser.Var_declContext) {
			newTexts.put(ctx, newTexts.get(ctx.var_decl()));
		} else if (ctx.getChild(0) instanceof MiniGoParser.Fun_declContext) {
			newTexts.put(ctx, newTexts.get(ctx.fun_decl()));
		}
	}

	@Override
	public void exitVar_decl(MiniGoParser.Var_declContext ctx) {
		if (ctx.getChildCount() == 3) {
			newTexts.put(ctx, newTexts.get(ctx.dec_spec()) + " " + ctx.getChild(1).getText() + " "
					+ newTexts.get(ctx.type_spec()));
		} else if (ctx.getChildCount() == 5) {
			newTexts.put(ctx, newTexts.get(ctx.dec_spec()) + " " + ctx.getChild(1).getText() + " "
					+ newTexts.get(ctx.type_spec()) + " = " + ctx.getChild(4).getText());
		} else if (ctx.getChildCount() == 6) {
			newTexts.put(ctx, newTexts.get(ctx.dec_spec()) + " " + ctx.getChild(1).getText() + " ["
					+ ctx.getChild(3).getText() + "] " + newTexts.get(ctx.type_spec()));
		}
	}

	@Override
	public void exitDec_spec(MiniGoParser.Dec_specContext ctx) {
		newTexts.put(ctx, ctx.getChild(0).getText());
	}

	@Override
	public void exitType_spec(MiniGoParser.Type_specContext ctx) {
		if (ctx.isEmpty()) {
			newTexts.put(ctx, " ");
		} else if (ctx.getChild(0) instanceof TerminalNodeImpl) {
			newTexts.put(ctx, ctx.getChild(0).getText());
		}
	}

	@Override
	public void exitFun_decl(MiniGoParser.Fun_declContext ctx) {
		if (ctx.getChildCount() == 7) {
			newTexts.put(ctx,
					ctx.getChild(0).getText() + " " + ctx.getChild(1).getText() + " (" + newTexts.get(ctx.params())
							+ ") " + newTexts.get(ctx.type_spec(0)) + " " + newTexts.get(ctx.compound_stmt()));
		} else if (ctx.getChildCount() == 11) {
			newTexts.put(ctx,
					ctx.getChild(0).getText() + " " + ctx.getChild(1).getText() + " (" + newTexts.get(ctx.params())
							+ ") " + " (" + newTexts.get(ctx.type_spec(0)) + " , " + newTexts.get(ctx.type_spec(1))
							+ ") " + newTexts.get(ctx.compound_stmt()));
		}
	}

	@Override
	public void exitParams(MiniGoParser.ParamsContext ctx) {
		if (ctx.getChild(0) instanceof MiniGoParser.ParamContext) {
			String s1 = newTexts.get(ctx.param(0));
			if (ctx.getChildCount() > 1) {
				for (int i = 1; i < ctx.getChildCount(); i++) {
					s1 += ", " + newTexts.get(ctx.getChild(i));
				}
			}
			newTexts.put(ctx, s1);
		} else {
			newTexts.put(ctx, " ");
		}
	}

	@Override
	public void exitParam(MiniGoParser.ParamContext ctx) {
		String s1 = ctx.getChild(0).getText();
		if (ctx.getChildCount() != 1) {
			s1 += newTexts.get(ctx.type_spec());
		}
		newTexts.put(ctx, s1);
	}

	@Override
	public void exitStmt(MiniGoParser.StmtContext ctx) {
		if (ctx.getChild(0) instanceof MiniGoParser.Expr_stmtContext) {
			newTexts.put(ctx, newTexts.get(ctx.expr_stmt()));
		} else if (ctx.getChild(0) instanceof MiniGoParser.Compound_stmtContext) {
			newTexts.put(ctx, newTexts.get(ctx.compound_stmt()));
		} else if (ctx.getChild(0) instanceof MiniGoParser.If_stmtContext) {
			newTexts.put(ctx, newTexts.get(ctx.if_stmt()));
		} else if (ctx.getChild(0) instanceof MiniGoParser.For_stmtContext) {
			newTexts.put(ctx, newTexts.get(ctx.for_stmt()));
		} else if (ctx.getChild(0) instanceof MiniGoParser.Return_stmtContext) {
			newTexts.put(ctx, newTexts.get(ctx.return_stmt()));
		}
	}

	@Override
	public void exitExpr_stmt(MiniGoParser.Expr_stmtContext ctx) {
		newTexts.put(ctx, newTexts.get(ctx.expr()));
	}

	@Override
	public void exitFor_stmt(MiniGoParser.For_stmtContext ctx) {
		if (ctx.getChild(1) instanceof MiniGoParser.Loop_exprContext) {
			newTexts.put(ctx,
					ctx.getChild(0).getText() + " " + newTexts.get(ctx.loop_expr()) + " " + newTexts.get(ctx.stmt()));
		} else if (ctx.getChild(1) instanceof MiniGoParser.ExprContext) {
			newTexts.put(ctx,
					ctx.getChild(0).getText() + " " + newTexts.get(ctx.expr()) + " " + newTexts.get(ctx.stmt()));
		}
	}

	@Override
	public void exitCompound_stmt(MiniGoParser.Compound_stmtContext ctx) {
		int ldCount = 0;
		int stmtCount = 0;
		String gap = "";
		String s1 = "{ \n";

		for (int i = 0; i < ctx.getChildCount(); i++) {
			if (ctx.getChild(i) instanceof MiniGoParser.Local_declContext) {
				ldCount++;
			}
			if (ctx.getChild(i) instanceof MiniGoParser.StmtContext) {
				stmtCount++;
			}
		}
		gap += "....";
		
		for (int i = 0; i < ldCount; i++) {
			
			s1 += gap + newTexts.get(ctx.local_decl(i)) + "\n";

		}
		for (int i = 0; i < stmtCount; i++) {
			s1 += gap + newTexts.get(ctx.stmt(i)) + "\n";
		}

		newTexts.put(ctx, s1 + "}");
	}

	@Override
	public void exitLocal_decl(MiniGoParser.Local_declContext ctx) {
		String s1 = newTexts.get(ctx.dec_spec()) + " " + ctx.getChild(1).getText();

		if (ctx.getChildCount() == 3) {
			newTexts.put(ctx, s1 + " " + newTexts.get(ctx.type_spec()));
		} else if (ctx.getChildCount() == 5) {
			newTexts.put(ctx, s1 + " " + newTexts.get(ctx.type_spec()) + " = " + ctx.getChild(4).getText());
		} else if (ctx.getChildCount() == 6) {
			newTexts.put(ctx, s1 + " [ " + ctx.getChild(3).getText() + " ] " + newTexts.get(ctx.type_spec()));
		}
	}

	@Override
	public void exitIf_stmt(MiniGoParser.If_stmtContext ctx) {
		String s1 = ctx.getChild(0).getText() + " " + newTexts.get(ctx.expr()) + " " + newTexts.get(ctx.stmt(0)) + " ";

		if (ctx.getChildCount() > 3) {
			s1 += ctx.getChild(3).getText() + " " + newTexts.get(ctx.stmt(1));
		}

		newTexts.put(ctx, s1);
	}

	@Override
	public void exitReturn_stmt(MiniGoParser.Return_stmtContext ctx) {
		String s1 = ctx.getChild(0).getText() + " ";

		if (ctx.getChildCount() >= 2) {
			s1 += newTexts.get(ctx.expr(0));
		}
		if (ctx.getChildCount() > 2) {
			s1 += " , " + newTexts.get(ctx.expr(1));
		}

		newTexts.put(ctx, s1);
	}

	@Override
	public void exitLoop_expr(MiniGoParser.Loop_exprContext ctx) {
		newTexts.put(ctx, newTexts.get(ctx.expr(0)) + "; " + newTexts.get(ctx.expr(1)) + "; "
				+ newTexts.get(ctx.expr(2)) + ctx.getChild(5).getText());
	}

	@Override
	public void exitExpr(MiniGoParser.ExprContext ctx) {
		String s1 = null, s2 = null, op = null;

		if (ctx.getChildCount() == 1) {
			newTexts.put(ctx, ctx.getChild(0).getText());
		}
		
		if(ctx.getChildCount() == 2) {
			newTexts.put(ctx, ctx.getChild(0).getText()+newTexts.get(ctx.expr(0)));
		}

		if (ctx.getChildCount() == 3) {
			if (ctx.getChild(0).getText().equals("(")) {
				newTexts.put(ctx, "(" + newTexts.get(ctx.expr(0)) + ")");
			} else if (ctx.getChild(1).getText().equals("=")) {
				newTexts.put(ctx, ctx.getChild(0).getText() + " = " + newTexts.get(ctx.expr(0)));
			} else if (ctx.getChild(1) != ctx.expr()) {
				// 예 : expr '+' expr
				s1 = newTexts.get(ctx.expr(0));
				s2 = newTexts.get(ctx.expr(1));
				op = ctx.getChild(1).getText();
				newTexts.put(ctx, s1 + " " + op + " " + s2);
			}
		}

		if (ctx.getChildCount() == 4) {
			if (ctx.getChild(1).getText().equals("[")) {
				newTexts.put(ctx, ctx.getChild(0).getText() + " [ " + newTexts.get(ctx.expr(0)) + " ] ");
			} else {
				newTexts.put(ctx, ctx.getChild(0).getText() + " (" + newTexts.get(ctx.args()) + ") ");
			}
		}

		if (ctx.getChildCount() == 6) {
			if (ctx.getChild(0) == ctx.FMT()) {
				newTexts.put(ctx, ctx.getChild(0).getText() + "." + ctx.getChild(2).getText() + " ("
						+ newTexts.get(ctx.args()) + ") ");
			} else {
				newTexts.put(ctx, ctx.getChild(0).getText() + " [ " + newTexts.get(ctx.expr(0)) + " ] = "
						+ newTexts.get(ctx.expr(1)));
			}
		}
	}

	@Override
	public void exitArgs(MiniGoParser.ArgsContext ctx) {
		String s1 = newTexts.get(ctx.expr(0));

		int temp = 0;
		if (ctx.getChildCount() > 1) {
			for (int i = 1; i < ctx.getChildCount() - 1; i++) {
				s1 += " , " + newTexts.get(ctx.expr(i));
			}

		}
		newTexts.put(ctx, s1);
	}
}
