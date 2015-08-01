package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;

public class Stone extends PointImpl implements State<Elements, Player>, Tickable{
    private Direction direction;

    public Stone(int x, int y) {
        super(x, y);
        direction = Direction.DOWN;
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        return Elements.STONE;
    }

    @Override
    public void tick() {

        if (direction != null) {

            int newX = direction.changeX(x);
            int newY = direction.changeY(y);
            move(newX, newY);
        }

    }

    public Stone getStone() {
        return this;
    }
}
