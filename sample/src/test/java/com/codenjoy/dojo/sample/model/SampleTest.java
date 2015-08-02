package com.codenjoy.dojo.sample.model;

import com.codenjoy.dojo.services.PrinterFactory;
import com.codenjoy.dojo.utils.TestUtils;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.PrinterFactoryImpl;
import org.junit.Before;
import org.junit.Ignore;
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
        dice(-1, -1);
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
        // given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");

        game.addStone(2);
        game.tick();

        // then
        assertE("☼ 0 ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }

    @Test
    public void shouldStoneMove() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        // when
        game.addStone(1);
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼   ☼" +
                "☼   ☼" +
                "☼0  ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");
    }

    @Test
    public void shouldStoneAppearsEvery3seconds() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        // when
        dice(0, -1);
        game.tick();
        game.tick();
        game.tick();

        dice(0, -1);
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼0  ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼0  ☼" +
                "☼ ☺ ☼");
    }

    @Test
    public void shouldHeroShoot() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

        // when
        hero.act();
        game.tick();
        game.tick();

        // then
        assertE("☼   ☼" +
                "☼ * ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");
    }

    @Test
    public void shouldStoneDestroyedByBullet() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        dice(1, -1);
        hero.act();
        game.tick();

        assertE("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ * ☼" +
                "☼ ☺ ☼");

        game.tick();

        assertE("☼   ☼" +
                "☼   ☼" +
                "☼ * ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        game.tick();

        assertE("☼ 0 ☼" +
                "☼ * ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        game.tick();

        // then
        assertE("☼ x ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

    }

    @Test
    public void shouldStoneDestroyedByBullet2() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

        dice(1, -1);
        hero.act();
        game.tick();

        assertE("☼   ☼" +
                "☼   ☼" +
                "☼ * ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

        game.tick();

        assertE("☼   ☼" +
                "☼ * ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

        game.tick();

        assertE("☼ x ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

        game.tick();

        // then
        assertE("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

    }

    @Test
    public void shouldStoneRemovedWhenOutsideBorder() {
        // given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        // when
        dice(2, -1, 2, -1);
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼  0☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        game.tick();
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼   ☼" +
                "☼  0☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺0☼");

        game.tick();

        // then
        assertE("☼   ☼" +
                "☼   ☼" +
                "☼  0☼" +
                "☼   ☼" +
                "☼ ☺ ☼");
    }

    @Test
    public void shouldBulletOutbord() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");

        // when
        hero.act();
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }

    @Test
    public void shouldNewBomb() {
        // given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");

        game.addBomb(2);
        game.tick();

        // then
        assertE("☼ ♣ ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }

    @Test
    public void shouldNewBombAtRandomPlace() {
        // given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");

        dice(-1, 1);
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼ ♣ ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }

    @Test
    public void shouldNewBombAndNewStoneAtNewPlace() {
        // given
        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");

        dice(1, 1, 1, 1, 1, 1, 2);
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼ 0♣☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼" +
                "☼   ☼");
    }


    @Test
    public void shouldBombDestroyedByBullet() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");


        dice(-1, 1);
        hero.act();
        game.tick();
        game.tick();
        game.tick();



        assertE("☼ ♣ ☼" +
                "☼ * ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

        game.tick();

        assertE("☼xxx☼" +
                "☼xxx☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼");

    }

    @Test
    public void shouldBombDestroyedByBullet2() {
        // given

        givenFl("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");


        dice(-1, 1);
        hero.act();
        game.tick();
        game.tick();




        assertE("☼   ☼" +
                "☼ * ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

        game.tick();

        assertE("☼xxx☼" +
                "☼xxx☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");

        game.tick();

        assertE("☼   ☼" +
                "☼   ☼" +
                "☼   ☼" +
                "☼ ☺ ☼" +
                "☼   ☼");
    }
    @Test
    public void shouldNewBombAndNewStoneAtNewPlace2() {
        // given
        givenFl("☼    ☼" +
                "☼    ☼" +
                "☼ ☺  ☼" +
                "☼    ☼" +
                "☼    ☼" +
                "☼    ☼");

        dice(1, 3);
        game.tick();
        game.tick();
        game.tick();

        // then
        assertE("☼ 0 ♣☼" +
                "☼    ☼" +
                "☼ ☺  ☼" +
                "☼    ☼" +
                "☼    ☼" +
                "☼    ☼");
    }


    @Test
    public void shouldBombDestroyedByBulletNew() {
        // given
        givenFl("☼    ☼" +
                "☼    ☼" +
                "☼    ☼" +
                "☼    ☼" +
                "☼    ☼" +
                "☼  ☺ ☼");

        dice(0, 2);

        game.tick();
        game.tick();
        hero.act();
        game.tick();
        game.tick();

        // then
        assertE("☼    ☼" +
                "☼0 ♣ ☼" +
                "☼    ☼" +
                "☼  * ☼" +
                "☼    ☼" +
                "☼  ☺ ☼");

        game.tick();


//        game.tick();
        // then
        assertE("☼    ☼" +
                "☼ xxx☼" +
                "☼0xxx☼" +
                "☼ xxx☼" +
                "☼    ☼" +
                "☼  ☺ ☼");

    }
}
















