package nz.co.noirland.zephcore.math;

public class ExponentialCalculator implements Calculator {

    private final double a;
    private final double b;
    private final double c;

    /**
     * Create an exponential equation in the form y = ae^(bx) + c
     */
    public ExponentialCalculator(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double getY(double x) {
        // y = ae^(bx) + c
        return a * Math.exp(b * x) + c;
    }

    @Override
    public double getX(double y) {
        // x = 1/b * (ln(y-c) - ln(a))
        double ln = Math.log(y - c) - Math.log(a);
        return ln / b;
    }
}
