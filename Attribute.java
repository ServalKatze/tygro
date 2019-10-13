class Attribute {
    public byte curValue;
    public byte maxValue;

    public Attribute(byte curValue, byte maxValue) {
        this.curValue = curValue;
        this.maxValue = maxValue;
    }

    public void setCur(byte val) {
        if (val > maxValue)
            val = maxValue;
        curValue = val;
    }

    public void increase(byte amount) {
        curValue += amount;
        if (curValue > maxValue)
            curValue = maxValue;
        else if (curValue < 0)
            curValue = 0;
    }

    public float getRatio() {
        if (maxValue == 0) return 0.0;
        return 1.0 * curValue / maxValue;
    }
}