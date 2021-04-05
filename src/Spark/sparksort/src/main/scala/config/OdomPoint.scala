package config

case class OdomPoint(val senderName: String, val secs: Long, val nsecs: Long, val x: Double, val y: Double, val z: Double) extends Ordered[OdomPoint] {
    override def compare(that: OdomPoint): Int = {
        if(this.secs < that.secs) { return -1 } 
        else if(this.secs > that.secs) { return +1 } 
        else {
            if(this.nsecs < that.nsecs) { return -1 }
            else if(this.nsecs > that.nsecs) { return +1 }
            else { return 0}
        }
    }
}

object OdomPoint {
    def apply(): OdomPoint = {
        OdomPoint("", 0, 0, Double.NaN, Double.NaN, Double.NaN)
    }
}
