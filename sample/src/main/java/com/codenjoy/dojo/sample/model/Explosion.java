package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.*;

/**
 * Created by Pyatnitskiy.a on 01.08.2015.
 */
public class Explosion extends PointImpl implements State<Elements, Player> {

    public Explosion(Point pt) {
        super(pt);
    }

    public Explosion(int x, int y) {
        super(x, y);
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        return Elements.EXPLOSION;
    }
}
