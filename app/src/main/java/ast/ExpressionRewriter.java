package ast;

import java.util.List;
import java.util.Map;

sealed interface Expr permits Add, Sub, Mul, Div, Const, Var {}
record Add(Expr left, Expr right) implements Expr {}
record Sub(Expr left, Expr right) implements Expr {}
record Mul(Expr left, Expr right) implements Expr {}
record Div(Expr left, Expr right) implements Expr {}
record Const(int value) implements Expr {}
record Var(String name) implements Expr {}

public class ExpressionRewriter {
  // An interface for an expression tree rewriting pass.
  interface ExprRewritingPass {
    Expr rewrite(Expr expr);
  }

  private List<ExprRewritingPass> passes;

  ExpressionRewriter(ExprRewritingPass... passes) {
    this.passes = List.of(passes);
  }

  // Register a pass with the rewriter.
  public ExpressionRewriter register(ExprRewritingPass pass) {
    passes.add(pass);
    return this;
  }

  // Rewrite an expression tree.
  public Expr rewrite(Expr expr) {
    for (ExprRewritingPass pass : passes) {
      expr = pass.rewrite(expr);
    }
    return expr;
  }


  // A pass which rewrites all variables to constants.
  static class ConstantRewritingPass implements ExprRewritingPass {
    private int constantValue;

    ConstantRewritingPass(int constantValue) {
      this.constantValue = constantValue;
    }

    public Expr rewrite(Expr expr) {
      System.out.println("Rewriting " + expr);
      return switch (expr) {
        case Add(Expr left, Expr right) -> new Add(rewrite(left), rewrite(right));
        case Sub(Expr left, Expr right) -> new Sub(rewrite(left), rewrite(right));
        case Mul(Expr left, Expr right) -> new Mul(rewrite(left), rewrite(right));
        case Div(Expr left, Expr right) -> new Div(rewrite(left), rewrite(right));
        case Const(int value) -> new Const(constantValue);
        case Var(String name) -> expr;
      };
    }
  }

  static class ConstantFoldingPass implements ExprRewritingPass {
    public Expr rewrite(Expr expr) {
      System.out.println("Folding " + expr);
      return switch (expr) {
        case Add(Const left, Const right) -> new Const(left.value() + right.value());
        case Sub(Const left, Const right) -> new Const(left.value() - right.value());
        case Mul(Const left, Const right) -> new Const(left.value() * right.value());
        case Div(Const left, Const right) -> new Const(left.value() / right.value());
        case Add(Expr left, Expr right) -> new Add(rewrite(left), rewrite(right));
        case Sub(Expr left, Expr right) -> new Sub(rewrite(left), rewrite(right));
        case Mul(Expr left, Expr right) -> new Mul(rewrite(left), rewrite(right));
        case Div(Expr left, Expr right) -> new Div(rewrite(left), rewrite(right));
        case Const(int value) -> expr;
        case Var(String name) -> expr;
      };
    }
  }
  static class Evaluator {
    public static int evaluate(Expr expr, Map<String, Integer> env) {
      return switch (expr) {
        case Add(Expr left, Expr right) -> evaluate(left, env) + evaluate(right, env);
        case Sub(Expr left, Expr right) -> evaluate(left, env) - evaluate(right, env);
        case Mul(Expr left, Expr right) -> evaluate(left, env) * evaluate(right, env);
        case Div(Expr left, Expr right) -> evaluate(left, env) / evaluate(right, env);
        case Const(int value) -> value;
        case Var(String name) -> env.get(name);
      };
    }
  }

  // A pass which folds simple constant expressions like 1 + 2 into 3.
  static class SimpleConstantFolderPass {
    public static Expr fold(Expr expr) {
      System.out.println("Folding " + expr);
      return switch (expr) {
        case Add(Const left, Const right) -> new Const(left.value() + right.value());
        case Sub(Const left, Const right) -> new Const(left.value() - right.value());
        case Mul(Const left, Const right) -> new Const(left.value() * right.value());
        case Div(Const left, Const right) -> new Const(left.value() / right.value());
        case Add(Expr left, Expr right) -> new Add(fold(left), fold(right));
        case Sub(Expr left, Expr right) -> new Sub(fold(left), fold(right));
        case Mul(Expr left, Expr right) -> new Mul(fold(left), fold(right));
        case Div(Expr left, Expr right) -> new Div(fold(left), fold(right));
        case Const(int value) -> expr;
        case Var(String name) -> expr;
      };
    }
  }
}