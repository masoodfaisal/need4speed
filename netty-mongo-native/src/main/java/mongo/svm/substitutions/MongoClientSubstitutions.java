package mongd.svm.substitutions;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;


/**
 * This is a simple {@link Substitute} for mongo driver to boot with native compile and perform very basic operations.
 *
 * @author fmasood@redhat.com
 */

@TargetClass(className = "com.mongodb.internal.connection.ClientMetadataHelper")
final class Target_com_mongod_internal_connection_ClientMetadataHelper {

    @Substitute
    private static String getCodeSourcePath() {
        return null;
    }

    @Substitute
    private static String getDriverVersion() {
        return "3.8.0-beta3-44-g1ff4ce53c-dirty";
    }




}



public class MongoClientSubstitutions {


}
