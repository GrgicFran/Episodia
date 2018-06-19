package com.dryice.episodia.utils;

import java.util.Random;

/**
 * Program generates a random episode from a random season of desired show
 *
 * @author Fran Grgić
 * @version 1.0.0
 *
 */
public class RandomEpisodeGenerator {
    /**Default number of seasons*/
    public static final int DEFAULT_SEASON_NUMBER = 10;
    /**Default number of episodes per one season of the show*/
    public static final int DEFAULT_EPISODES_NUMBER = 20;

    private int randomSeason, randomEpisode;
    private Random randomGenerator = new Random();


    /**
     * Creates an object using the default values
     */
    public RandomEpisodeGenerator(){
        this(DEFAULT_SEASON_NUMBER, DEFAULT_EPISODES_NUMBER);
    }

    /**
     * Creates an object using two parameters
     *
     * @param numberOfSeasons number of seasons the show has
     * @param numberOfEpisodes number of episodes per season
     */
    public RandomEpisodeGenerator(int numberOfSeasons, int numberOfEpisodes){
        setRandomSeason(numberOfSeasons);
        setRandomEpisode(numberOfEpisodes);
    }

    /**
     * Sets the value of <code>randomSeason</code>
     * @param numberOfSeasons number of seasons the show has
     */
    public void setRandomSeason(int numberOfSeasons){
        if(numberOfSeasons > 0)
            this.randomSeason = randomGenerator.nextInt(numberOfSeasons) + 1;
        else{
            IndexOutOfBoundsException e = new IndexOutOfBoundsException("Sjebo si");
            throw e;
        }
    }

    /**
     * Sets the value of <code>randomEpisode</code>
     * @param numberOfEpisodes number of episodes per season of the show
     */
    public void setRandomEpisode(int numberOfEpisodes){
        if (numberOfEpisodes>0){
            this.randomEpisode = randomGenerator.nextInt(numberOfEpisodes) + 1;
        }else{
            IndexOutOfBoundsException e = new IndexOutOfBoundsException("Jebiga");
            throw e;
        }
    }

    /**
     * gets the value of <code>randomSeason</code>
     * @return random season number
     */
    public int getRandomSeason(){
        return randomSeason;
    }

    /**
     * gets the value of <code>randomEpisode</code>
     * @return random episode number
     */
    public int getRandomEpisode(){
        return randomEpisode;
    }

}
