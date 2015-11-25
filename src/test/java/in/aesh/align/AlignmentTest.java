package in.aesh.align;

import com.google.common.base.Splitter;
import edu.cmu.sphinx.alignment.LongTextAligner;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class AlignmentTest {
    
    @Test
    public void testAlignment() {
        Splitter splitter = Splitter.on(" ").omitEmptyStrings().trimResults();
        String full = "master cleanse cronut freegan poutine chicharrones kinfolk dreamcatcher ugh cray normcore thundercats you probably haven't heard of them letterpress hammock lomo direct trade authentic irony neutra literally tattooed shoreditch typewriter try-hard cray blog selfies thundercats schlitz paleo umami etsy street art mlkshk food truck echo park deep v selvage hella bitters";
        String aligned = "poutine chicharrones kinfolk heard of them letterpress hammock echo park";
        
        LongTextAligner aligner = new LongTextAligner(splitter.splitToList(aligned), 1);
        int[] results = aligner.align(splitter.splitToList(full));
        
        assertThat(results, equalTo(new int[]{-1,-1,-1,-1,0,1,2,-1,-1,-1,-1,-1,-1,-1,-1,3,4,5,6,7,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,8,9,-1,-1,-1,-1,-1}));
    }
}