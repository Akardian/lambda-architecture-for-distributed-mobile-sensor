package producer;

import java.util.ArrayList;
import java.util.List;

public class TestData {
    
    public String sourceName;
    public String sourceType;

    public List<String> message;

    public TestData() {
        sourceName = "data-generator-1";
        sourceType = "data-generator";

        message = new ArrayList<String>();
        message.add("Hello Bob");
        message.add("Hello Sue");
    }
}
