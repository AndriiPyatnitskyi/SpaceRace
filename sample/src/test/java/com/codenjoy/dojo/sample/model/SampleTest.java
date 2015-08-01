package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.PrinterFactory;
import com.codenjoy.dojo.utils.TestUtils;
import com.codenjoy.dojo.sample.services.Events;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.PrinterFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * User: sanja
 * Date: 17.12.13
 * Time: 4:47
 */
public class SampleTest {

    private Sample game;
    private Hero hero;
    private Dice dice;
    private EventListener listener;
    private Player player;
    private PrinterFactory printer = new PrinterFactoryImpl();

    @Before
    public void setup() {
        dice = mock(Dice.class);
    }

    private void dice(int...ints) {
        OngoingStubbing<Integer> when = when(dice.next(anyInt()));
        for (int i : ints) {
            when = when.thenReturn(i);
        }
    }

    private void givenFl(String board) {
        LevelImpl level = new LevelImpl(board);
        Hero hero = level.getHero().get(0);

        game = new Sample(level, dice);
        listener = mock(EventListener.class);
        player = new Player(listener);
        game.newGame(player);
        player.hero = hero;
        hero.init(game);
        this.hero = game.getHeroes().get(0);
    }

    private void assertE(String expected) {
        assertEquals(TestUtils.injectN(expected),
                printer.getPrinter(game.reader(), player).print());
    }




    // есть карта со мной
    @Test
    public void shouldFieldAtStart() {
        //Given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");

        assertE("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }

    // я могу двигаться
    @Test
    public void shouldFieldIcanMove() {
        //Given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
        //when
        hero.up();
        game.tick();

        //then
        assertE("☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼");
        //when
        hero.right();
        game.tick();

        //then
        assertE("☼   ☼" +
                "☼  ☺☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼");
        //when
        hero.down();
        game.tick();

        //then
        assertE("☼   ☼" +
                "☼   ☼" +
                "☼  ☺☼" +
                "☼   ☼" +
                "☼   ☼");
        //when
        hero.left();
        game.tick();

        //then
        assertE("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }

    @Test
    public void shouldNewStone() {
        //Given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");

        dice(1);
        game.tick();

        assertE("☼ 0 ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }
//    // Я умею стралять в любую позицию
//    @Test
//    public void shouldFireUp() {
//        givenFl("☼☼☼☼☼" +
//                "☼   ☼" +
//                "☼ ☺ ☼" +
//                "☼   ☼" +
//                "☼☼☼☼☼");
//
//        hero.act();
//        hero.up();
//
//        game.tick();
//
//        assertE("☼☼☼☼☼" +
//                "☼ * ☼" +
//                "☼ ☺ ☼" +
//                "☼   ☼" +
//                "☼☼☼☼☼");
//    }
//
//    // если снарядом выстрелили он летит
//    @Test
//    public void shouldFiredBulletCanFly() {
//        givenFl("☼☼☼☼☼" +
//                "☼   ☼" +
//                "☼   ☼" +
//                "☼ ☺ ☼" +
//                "☼☼☼☼☼");
//
//        hero.act();
//        hero.up();
//
//        game.tick();
//
//        assertE("☼☼☼☼☼" +
//                "☼   ☼" +
//                "☼ # ☼" +
//                "☼ ☺ ☼" +
//                "☼☼☼☼☼");
//
//        game.tick();
//
//        assertE("☼☼☼☼☼" +
//                "☼ # ☼" +
//                "☼   ☼" +
//                "☼ ☺ ☼" +
//                "☼☼☼☼☼");
//    }
//
//    // что если снаряд попал на стенку
//    @Test
//    public void shouldBulletDestroyOnWall() {
//        // Given
//        givenFl("☼☼☼☼☼" +
//                "☼   ☼" +
//                "☼ ☺ ☼" +
//                "☼   ☼" +
//                "☼☼☼☼☼");
//
//        hero.act();
//        hero.up();
//
//        game.tick();
//
//        assertE("☼☼☼☼☼" +
//                "☼ # ☼" +
//                "☼ ☺ ☼" +
//                "☼   ☼" +
//                "☼☼☼☼☼");
//
//        // when
//        game.tick();
//
//        // then
//        assertE("☼☼☼☼☼" +
//                "☼   ☼" +
//                "☼ ☺ ☼" +
//                "☼   ☼" +
//                "☼☼☼☼☼");
//    }

    //на борде есть только стенки слева и справа

    //на борде есть корабль
    //может передвигаться вправо
    //может передвигаться влево
    //может передвигаться вверх
    //может передвигаться вниз




}
















