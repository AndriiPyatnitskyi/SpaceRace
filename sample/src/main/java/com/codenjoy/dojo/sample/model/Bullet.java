package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;

/**
 * Created by indigo on 01.08.2015.
 */
public class Bullet extends PointImpl implements State<Elements, Player> {

    public Bullet(Point pt) {
        super(pt);
    }

    public Bullet(int x, int y) {
        super(x, y);
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        return Elements.BULLET;
    }
}
