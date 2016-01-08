package in.com.v2kart.dataimport.strategies;

import in.com.v2kart.dataimport.dto.BaseDto;
import in.com.v2kart.dataimport.importengine.FileReader;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Ordering;

/**
 * FeedReaderStrategy - strategy to read csv feeds from CSV reader
 * 
 */
public interface FeedReaderStrategy {

    /**
     * readFeed function to read csv file and populate a list of dtos
     * 
     * @param reader
     * @return List of Dtos
     */
    <T extends BaseDto> List<T> readFeed(final FileReader reader);

    /**
     * Function to retrieve feeds based upon the filtered criteria
     * 
     * @param reader
     * @param predicate
     * @return List of Dtos
     */
    <T extends BaseDto> List<T> readFeed(FileReader reader, Predicate<T> predicate);

    /**
     * Function to retrieve feeds based upon the sorting criteria
     * 
     * @param reader
     * @param ordering
     * @return Collection of Dtos
     */
    <T extends BaseDto> Collection<T> readFeed(FileReader reader, Ordering<T> ordering);

    /**
     * Function to retrieve feeds based upon the sorting and filtered criteria
     * 
     * @param reader
     * @param ordering
     * @param predicate
     * @return Collection of Dtos
     */
    <T extends BaseDto> Collection<T> readFeed(FileReader reader, Ordering<T> ordering, Predicate<T> predicate);

}
