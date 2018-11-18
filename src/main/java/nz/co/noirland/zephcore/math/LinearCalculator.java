package nz.co.noirland.zephcore.math;

public class LinearCalculator implements Calculator {

    private final double m;
    private final double c;

    /**
     * Creates a linear equation in the form y = mx + c
     */
    public LinearCalculator(double m, double c) {
        this.m = m;
        this.c = c;
    }

    @Override
    public double getY(double x) {
        // y = mx + c
        return (m * x) + c;
    }

    @Override
    public double getX(double y) {
        // x = (y-c)/m
        return (y - c) / m;
    }
}
