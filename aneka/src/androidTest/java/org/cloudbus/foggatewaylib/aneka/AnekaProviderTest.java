package org.cloudbus.foggatewaylib.aneka;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.manjrasoft.aneka.ArrayOfFile;
import com.manjrasoft.aneka.ArrayOfTaskItem;

import org.apache.commons.net.ftp.FTP;
import org.cloudbus.foggatewaylib.core.ExecutionManager;
import org.cloudbus.foggatewaylib.core.GenericData;
import org.cloudbus.foggatewaylib.core.IndividualTrigger;
import org.cloudbus.foggatewaylib.core.Store;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class AnekaProviderTest {
    boolean end = false;

    @Test
    public void test1(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        SimpleAnekaProvider anekaProvider = new SimpleAnekaProvider();

        final String inputString = getRandomString(256);

        ExecutionManager executionManager = new ExecutionManager(appContext);

        executionManager.addProvider("ANEKA", "OUTPUT", anekaProvider);

        executionManager.addTrigger("OUTPUT",
                "TRIGGER",
                new IndividualTrigger<GenericData>(GenericData.class){

                    @Override
                    public void onNewData(Store<GenericData> store, GenericData data) {
                        System.out.println(data.getValue());
                        assertEquals(inputString + "ZA WARUDO", data.getValue());
                        end = true;
                    }
        });

        while (!anekaProvider.isConnected()){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        executionManager.runProvider("ANEKA",
                ExecutionManager.nextRequestID(),
                new GenericData<>(inputString)
                );

        while (!end){
            try {
                Log.d("DEBUG", "Still nothing mate");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class SimpleAnekaProvider extends AnekaProvider<GenericData, GenericData> {

        public SimpleAnekaProvider() {
            super(FTP.ASCII_FILE_TYPE, 1, GenericData.class, GenericData.class);
            inputVirtualPath = "input.txt";
            outputVirtualPath = "output.txt";
        }

        @Override
        protected ArrayOfTaskItem buildTasks() {
            return WSDLBuilder.buildArrayOfTaskItem(
                    WSDLBuilder.buildExecuteTaskItem(
                            "python",
                            "test.py"
                    )
            );
        }

        @Override
        protected void initCredentials() {
            username = Credentials.USERNAME;
            password = Credentials.PASSWORD;
            url = Credentials.URL;
            appName = "appetta";
        }

        @Override
        protected ArrayOfFile initSharedFiles() {
            return WSDLBuilder.buildArrayOfFile("FTP", "/",
                    "",
                    new String[]{
                            "shared.txt",
                            "test.py"
                    }
            );
        }

        @Override
        protected StorageBucket[] initStorageBuckets() {
            return new StorageBucket[]{
                    new FTPStorageBucket(
                            "FTP",
                            Credentials.USERNAME,
                            Credentials.PASSWORD,
                            Credentials.HOST
                    )
            };
        }

        @Override
        protected void onLogin(AnekaWebServices anekaWebServices, boolean error) {
            Log.d("DEBUG", "Login is " + (!error));
            if (anekaWebServices == null)
                Log.d("DEBUG", "Webservices is null :(");
            else if (anekaWebServices.getError() != null){
                Log.d("DEBUG", anekaWebServices.getError());
            } else{
                Log.d("DEBUG", anekaWebServices.getUserCredential().getValue());
            }
        }

        @Override
        protected byte[] inputToBytes(GenericData... input) {
            return null;
        }

        @Override
        protected String inputToString(GenericData... input) {
            return ((GenericData<String>) input[0]).getValue();
        }

        @Override
        protected GenericData[] bytesToOutput(byte[] bytes) {
            return null;
        }

        @Override
        protected GenericData[] stringToOutput(String string) {
            return new GenericData[]{new GenericData<>(string)};
        }
    }


    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    /**
     * @see <a href="https://stackoverflow.com/a/12116194">https://stackoverflow.com/a/12116194</a>
     */
    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }



}