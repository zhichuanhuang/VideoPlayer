// AidlWebActivity.aidl
package ucweb.video;

// Declare any non-default types here with import statements

interface AidlWebActivity {

    void playMemoryRecord(String hash, int dur, int currPos);

    void currNumInt(String hash, int numInt);

    void statistics();
}
