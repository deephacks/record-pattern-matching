package ast;

import ast.ExpressionRewriter.ConstantFoldingPass;
import ast.ExpressionRewriter.ConstantRewritingPass;
import ast.ExpressionRewriter.Evaluator;

import java.util.Map;

public class Main {
  public static void main(String[] args) {
    ExpressionRewriter rewriter = new ExpressionRewriter(
      new ConstantFoldingPass(),
      new ConstantRewritingPass(42));

    // Create an expression tree for 1 + 2 * 3
    Expr expr = new Add(new Const(1), new Mul(new Const(2), new Const(3)));

    // Print the expression tree
    System.out.println("Original: " + expr);

    // Evaluate the expression tree
    System.out.println("Result: " + Evaluator.evaluate(expr, Map.of()));

    // Now rewrite the expression tree
    Expr rewritten = rewriter.rewrite(expr);

    // Print the rewritten expression tree
    System.out.println("Rewritten: " + rewritten);

    // Evaluate the rewritten expression tree
    System.out.println("Result: " + Evaluator.evaluate(rewritten, Map.of()));
  }
}
