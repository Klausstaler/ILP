package uk.ac.ed.inf;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.OptimizationProblemType;
import com.google.ortools.linearsolver.MPVariable;

public class OrToolsTest {

    /**
     * Adapted from https://developers.google.com/optimization/introduction/using
     *
     */
    public void linear() {
        final MPSolver solver = new MPSolver("LinearExample", OptimizationProblemType.GLOP_LINEAR_PROGRAMMING);
        final double infinity = MPSolver.infinity();

        // x is a continuous non-negative variable
        final MPVariable x = solver.makeNumVar(0.0, infinity, "x");

        // Maximize x
        final MPObjective objective = solver.objective();
        objective.setCoefficient(x, 1);
        objective.setMaximization();

        // x <= 10
        final MPConstraint c = solver.makeConstraint(-infinity, 10.0);
        c.setCoefficient(x, 1);

        solver.solve();

        assertEquals(1, solver.numVariables());
        assertEquals(1, solver.numConstraints());
        assertEquals(10, solver.objective().value(), 0.001);
        assertEquals(10, x.solutionValue(), 0.001);
    }

    @Test
    public void test() {
        System.out.println("Loading native library jniortools.");
        System.loadLibrary("jniortools");
        System.out.println("Running the stuff.");
        linear();
    }

}