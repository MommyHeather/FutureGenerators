package co.uk.mommyheather.futuregenerators.util;

import net.minecraft.core.Direction;

public enum BlockSegment {

    NORTH(Direction.NORTH, 0D, 0D, -1D),
    SOUTH(Direction.SOUTH, 0D, 0D, 1D),
    EAST(Direction.EAST, 1D, 0D, 0D),
    WEST(Direction.WEST, -1D, 0D, 0D),
    UP(Direction.UP, 0D, 1D, 0D),
    DOWN(Direction.DOWN, 0D, -1D, 0D),
    CENTRE(null, 0D, 0D, 0D); //not a huge fan of this, but it does the job nicely and i can't think of a better option
    
    private final Direction direction;
    private final double xOffset;
    private final double yOffset;
    private final double zOffset;


    private BlockSegment(Direction direction, double xOffset, double yOffset, double zOffset) {
        this.direction = direction;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
    }


    public Direction getDirection() {
        return direction;
    }


    public double getxOffset() {
        return xOffset;
    }


    public double getyOffset() {
        return yOffset;
    }


    public double getzOffset() {
        return zOffset;
    }

    public double xWithMult(double in) {
        return xOffset * in;
    }

    public double yWithMult(double in) {
        return yOffset * in;
    }

    public double zWithMult(double in) {
        return zOffset * in;
    }
    

}
