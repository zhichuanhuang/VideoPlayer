// AidlRemoteService.aidl
package ucweb.video;

// Declare any non-default types here with import statements
import ucweb.video.AidlWebActivity;

interface AidlRemoteService {

    void registerWebCall(AidlWebActivity cb);
}
