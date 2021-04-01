package aggregations

import config.Config._

object TimeOrdering extends Ordering[OdomPoint] {
    def compare(element1:OdomPoint, element2:OdomPoint): Int = {
        if(element1.secs < element2.secs) { return -1 } 
        else if(element1.secs > element2.secs) { return +1 } 
        else {
            if(element1.nsecs < element2.nsecs) { return -1 }
            else if(element1.nsecs > element2.nsecs) { return +1 }
            else { return 0}
        }
    }
}
