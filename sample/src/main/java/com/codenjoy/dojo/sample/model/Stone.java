package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;
import com.codenjoy.dojo.services.Tickable;

/**
 * Created by Pyatnitskiy.a on 01.08.2015.
 */
public class Stone extends PointImpl implements State<Elements, Player>, Tickable{
    private Direction direction;

    public Stone(int x, int y) {
        super(x, y);
        direction = null;
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
        }else {
            direction = direction.DOWN;
        }

    }

    public Stone getStone() {
        return this;
    }
}
