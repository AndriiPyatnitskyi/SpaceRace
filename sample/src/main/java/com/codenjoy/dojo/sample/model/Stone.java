package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.services.State;

/**
 * Created by Pyatnitskiy.a on 01.08.2015.
 */
public class Stone extends PointImpl implements State<Elements, Player>{

    public Stone(int x, int y) {
        super(x, y);
    }

    @Override
    public Elements state(Player player, Object... alsoAtPoint) {
        return Elements.STONE;
    }
}
