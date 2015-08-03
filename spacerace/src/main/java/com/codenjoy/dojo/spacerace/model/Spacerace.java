package com.codenjoy.dojo.spacerace.model;

import com.codenjoy.dojo.services.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;

/**
 * О! Это самое сердце игры - борда, на которой все происходит.
 * Если какой-то из жителей борды вдруг захочет узнать что-то у нее, то лучше ему дать интефейс {@see Field}
 * Борда реализует интерфейс {@see Tickable} чтобы быть уведомленной о каждом тике игры. Обрати внимание на {Spacerace#tick()}
 */
public class Spacerace implements Tickable, Field {

    public static final int NEW_APPEAR_PERIOD = 3;
    private List<Player> players;

    private final int size;
    private Dice dice;
    private List<Wall> walls;
    private List<Bullet> bullets;
    private List<Explosion> explosions;
    private List<Stone> stones;
    private boolean isNewStone = true;
    private int countStone = 0;
    private int countBomb = 0;
    private List<Bomb> bombs;
    private boolean isNewBomb = true;

    public Spacerace(Level level, Dice dice) {
        this.dice = dice;
        walls = level.getWalls();
        size = level.getSize();
        players = new LinkedList<>();
        stones = new LinkedList<>();
        bombs = new LinkedList<>();
        bullets = new LinkedList<>();
        explosions = new LinkedList<>();

    }

    /**
     * @see Tickable#tick()
     */
    @Override
    public void tick() {
//        for(Player player : players){
//            if(player.getHero().isAlive()){
//                newGame(new Player());
//            }
//        }
        explosions.clear();
        createStone();
        createBomb();
        tickHeroes();
        tickBullets();
        tickBombs();
        tickStones();
        removeStoneOutOfBoard();
        removeBulleteOutOfBoard();
    }

    private void createBomb() {
        countBomb++;
        if (countBomb == NEW_APPEAR_PERIOD) {
            int count = 0;
            while (count++ < 10000) {
                int x = dice.next(size - 2);
                if (x == -1) break;
                if (stones.contains(pt(x + 1, size))) continue;

                addBomb(x + 1);
                countBomb = 0;
                break;
            }
            if (count == 10000) {
                throw new RuntimeException("Извините не нашли пустого места");
            }
        }
    }

    private void tickBombs() {
        removeBombDestroyedByBullet();
        for (Bomb bomb : bombs) {
            bomb.tick();
        }
        removeBombDestroyedByBullet();
        heroExploytedByBomb();
    }

    private void createStone() {
        countStone++;
        if (countStone == NEW_APPEAR_PERIOD) {
            int x = dice.next(size - 2);
            if (x != -1) {
                addStone(x + 1);
                countStone = 0;
            }
        }
    }

    private void tickHeroes() {
        for (Player player : players) {
            Hero hero = player.getHero();
            hero.tick();
        }
    }

    private void tickBullets() {
        for (Bullet bullet : bullets) {
            bullet.tick();
        }
    }

    private void tickStones() {
        removeStoneDestroyedByBullet();
        for (Stone stone : stones) {
            stone.tick();
        }
        removeStoneDestroyedByBullet();
    }

    private void removeStoneOutOfBoard() {
        for (Bullet bullet : new ArrayList<>(bullets)){
            if (bullet.isOutOf(size)){
                bullets.remove(bullet);
            }
        }
    }

    private void removeBulleteOutOfBoard() {
        for (Stone stone : new ArrayList<>(stones)){
            if (stone.isOutOf(size)){
                stones.remove(stone);
            }
        }
    }

    private void removeStoneDestroyedByBullet() {
        for (Bullet bullet : new ArrayList<>(bullets)) { // TODO to use iterator.remove
            if (stones.contains(bullet)) {
                bullets.remove(bullet);
                stones.remove(bullet);
                explosions.add(new Explosion(bullet));
            }
        }
    }

    private void removeBombDestroyedByBullet() {
        for (Bullet bullet : new ArrayList<>(bullets)) { // TODO to use iterator.remove
            if (bombs.contains(bullet)) {
                bombs.remove(bullet);
                bullets.remove(bullet);
                bombExplosion(bullet);
            }
        }
    }

    private void heroExploytedByBomb() {
        List<Hero> heroes  = new LinkedList<>();
        for(Player player : players){
            heroes.add(player.getHero());
        }
        isNearBomb(heroes);

    }

    private void isNearBomb(List<Hero> heroes) {
        for (Bomb bomb : bombs) {
            for (Hero hero :  new ArrayList<>(heroes)) {
                for (int x = bomb.getX() - 1; x < bomb.getX() + 2; x++) {
                    for (int y = bomb.getY() - 1; y < bomb.getY() + 2; y++) {
                        Point p = new Bullet(x, y);
                        if (p.equals(hero)) {
                            bombExplosion(bomb);
                            bombs.remove(bomb);
                            hero.die();
                            heroes.remove(hero);
                        }
                    }
                }
            }
//            for (Bullet bullet : new ArrayList<>(bullets)){
//                if (bullet.isOutOf(size)){
//                    bullets.remove(bullet);
//                }
//            }
        }
    }



    private void bombExplosion(Point pt) {
        for(int x = pt.getX() - 1; x < pt.getX() + 2; x++){
            for(int y = pt.getY() - 1; y < pt.getY() + 2; y++){
                if (y != size) {
                    explosions.add(new Explosion(x, y));
                }
            }
        }
    }

    public int size() {
        return size;
    }

    public void newGame(Player player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    public void remove(Player player) {
        players.remove(player);
    }

    public BoardReader reader() {
        return new BoardReader() {
            private int size = Spacerace.this.size;

            @Override
            public int size() {
                return size;
            }

            @Override
            public Iterable<? extends Point> elements() {
                List<Point> result = new LinkedList<>();
                result.addAll(explosions);
                result.addAll(getHeroes());
                result.addAll(stones);
                result.addAll(bombs);
                result.addAll(bullets);
                result.addAll(walls);
                return result;
            }
        };
    }

    public List<Hero> getHeroes() {
        List<Hero> heroes = new LinkedList<>();
        for (Player player : players) {
           heroes.add(player.getHero());
        }
        return heroes;
    }

    @Override
    public void addBullet(int x, int y) {
        bullets.add(new Bullet(x, y));
    }

    @Override
    public void addStone(int x) {
        stones.add(new Stone(x, size));
        isNewStone = false;
    }

    public void addBomb(int x) {
        bombs.add(new Bomb(x, size));
        isNewBomb = false;
    }
}
















