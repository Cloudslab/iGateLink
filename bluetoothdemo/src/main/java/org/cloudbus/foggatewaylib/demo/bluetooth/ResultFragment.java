package org.cloudbus.foggatewaylib.demo.bluetooth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.cloudbus.foggatewaylib.core.ExecutionManager;
import org.cloudbus.foggatewaylib.core.IndividualTrigger;
import org.cloudbus.foggatewaylib.core.ProgressData;
import org.cloudbus.foggatewaylib.core.Store;
import org.cloudbus.foggatewaylib.service.FogGatewayService;
import org.cloudbus.foggatewaylib.service.FogGatewayServiceActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.cloudbus.foggatewaylib.demo.bluetooth.MainActivity.KEY_DATA_INPUT;
import static org.cloudbus.foggatewaylib.demo.bluetooth.MainActivity.KEY_DATA_OUTPUT;


public class ResultFragment extends Fragment {
    public static final String TAG = "Result Fragment";
    private static final int MAX_DATA = 60;

    private GraphView graph;
    private TextView textBPM;
    private TextView textSpO2;
    private TextView textPI;
    private TextView textAHI;
    private TextView textMinSpO2;
    private TextView textSeverity;
    private TextView textMinBPM;
    private TextView textMaxBPM;
    private TextView textAvgBPM;
    private TextView textDiagnosis;
    private ContentLoadingProgressBar progressBar;
    private Button button;

    private long device;

    private LineGraphSeries<DataPoint> seriesBPM;
    private LineGraphSeries<DataPoint> seriesSpO2;

    private long time;

    public ResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);

        graph = rootView.findViewById(R.id.graph_bpm);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setMargin(40);

        seriesBPM = new LineGraphSeries<>();
        graph.addSeries(seriesBPM);

        seriesBPM.setTitle("bpmPR");
        seriesBPM.setColor(Color.BLUE);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(MAX_DATA);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(255);
        graph.getViewport().setMinY(30);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.BLUE);

        seriesSpO2 = new LineGraphSeries<>();
        graph.getSecondScale().addSeries(seriesSpO2);

        seriesSpO2.setColor(Color.RED);
        seriesSpO2.setTitle("%SpO2");

        graph.getSecondScale().setMinY(70);
        graph.getSecondScale().setMaxY(100);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.RED);

        textBPM = rootView.findViewById(R.id.text_bpm);
        textSpO2 = rootView.findViewById(R.id.text_spo2);
        textPI = rootView.findViewById(R.id.text_pi);
        textAHI = rootView.findViewById(R.id.ahi);
        textMinSpO2 = rootView.findViewById(R.id.minspo2);
        textSeverity = rootView.findViewById(R.id.severity);
        textMinBPM = rootView.findViewById(R.id.minbpm);
        textMaxBPM = rootView.findViewById(R.id.maxbpm);
        textAvgBPM = rootView.findViewById(R.id.avgbpm);
        textDiagnosis = rootView.findViewById(R.id.diagnosis);

        button = rootView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null){
                    ExecutionManager.Holder holder = (ExecutionManager.Holder) getActivity();
                    if (holder.getExecutionManager() != null){
                        ExecutionManager em = holder.getExecutionManager();
                        // getting data from last hour
                        OximeterData[] data = ((Store<OximeterData>)em.getStore(KEY_DATA_INPUT))
                                .retrieveIntervalFrom(
                                        new Date().getTime()-3600000L,
                                        device);
                        holder.getExecutionManager()
                                .produceData(KEY_DATA_OUTPUT, device, data);
                    }

                }
            }
        });

        progressBar = rootView.findViewById(R.id.progress_bar);
        progressBar.setIndeterminate(true);

        return rootView;
    }


    @Override
    public void onAttach(@NonNull final Context context) {
        super.onAttach(context);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() == null)
            return;

        if (getArguments() != null){
            String deviceAddress = getArguments().getString("device_address");
            if (deviceAddress == null)
                throw new RuntimeException("Missing argument device_address");
            device = Long.valueOf(deviceAddress.replaceAll(":", ""), 16);
        } else
            throw new RuntimeException("Missing arguments");

        time = new Date().getTime();
        textBPM.setText("-");
        textSpO2.setText("-");
        textPI.setText("-");
        textAHI.setText("?");
        textMinSpO2.setText("?");
        textSeverity.setText("?");
        textMaxBPM.setText("?");
        textMinBPM.setText("?");
        textAvgBPM.setText("?");
        textDiagnosis.setText("?");
        progressBar.setVisibility(View.INVISIBLE);

        ExecutionManager executionManager
                = ((ExecutionManager.Holder)getActivity()).getExecutionManager();
        if (executionManager != null){
            initExecutionManager(executionManager);
        } else{
            seriesBPM.resetData(new DataPoint[0]);
            seriesSpO2.resetData(new DataPoint[0]);
            ((FogGatewayServiceActivity)getActivity())
                    .addServiceConnectionListener("resultFragment",
                    new FogGatewayServiceActivity.ServiceConnectionListener() {
                        @Override
                        public void onServiceConnected(FogGatewayService service) {
                            initExecutionManager(service.getExecutionManager());
                        }

                        @Override
                        public void onServiceDisconnected() { }
                    });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() == null)
            return;

        ExecutionManager executionManager
                = ((ExecutionManager.Holder)getActivity()).getExecutionManager();
        if (executionManager != null){
            executionManager.removeUITrigger("inputUpdateUI");
            executionManager.removeUITrigger("outputUpdateUI");
            executionManager.removeUITrigger("progressUpdateUI");
        }
        ((FogGatewayServiceActivity)getActivity())
                .removeServiceConnectionListener("resultFragment");
    }

    @SuppressWarnings("unchecked")
    private void initExecutionManager(ExecutionManager executionManager){
        executionManager.addUITrigger(KEY_DATA_INPUT,
                "inputUpdateUI",
                device,
                new IndividualTrigger<OximeterData>(OximeterData.class) {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onNewData(Store store, OximeterData data) {
                        double x = (data.getId()-time)/1000d;

                        if (data.getBPM() != -1){
                            try {
                                seriesBPM.appendData(
                                        new DataPoint(x, data.getBPM()),
                                        true,
                                        MAX_DATA);
                            } catch (IllegalArgumentException e){
                                e.printStackTrace();
                            }
                            textBPM.setText(String.valueOf(data.getBPM()));
                        } else{
                            textBPM.setText("-");
                        }
                        if (data.getSpO2() != -1){
                            try{
                                seriesSpO2.appendData(
                                        new DataPoint(x, data.getSpO2()),
                                        true,
                                        MAX_DATA);
                            } catch (IllegalArgumentException e){
                                e.printStackTrace();
                            }
                            textSpO2.setText(String.valueOf(data.getSpO2()));
                        } else{
                            textSpO2.setText("-");
                        }
                        if (data.getPI()  != -1f){
                            textPI.setText(String.valueOf(data.getPI()));
                        } else{
                            textPI.setText("-");
                        }
                    }
                });

        executionManager.addUITrigger(KEY_DATA_OUTPUT,
                "outputUpdateUI",
                device,
                new IndividualTrigger<AnalysisResultData>(AnalysisResultData.class) {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onNewData(Store store, AnalysisResultData data) {
                        textAHI.setText(String.valueOf(data.AHI));
                        textMinSpO2.setText(String.valueOf(data.minSpO2));
                        textSeverity.setText(data.AHSeverity);
                        textMaxBPM.setText(String.valueOf(data.maxBPM));
                        textMinBPM.setText(String.valueOf(data.minBPM));
                        textAvgBPM.setText(String.valueOf(data.avgBPM));
                        textDiagnosis.setText(data.EKGDiagnosis);

                    }
                });
        executionManager.addUITrigger(ExecutionManager.KEY_DATA_PROGRESS,
                "progressUpdateUI",
                device,
                new IndividualTrigger<ProgressData>(ProgressData.class) {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onNewData(Store store, ProgressData data) {
                        switch (data.getProgress()){
                            case 0:
                                progressBar.setVisibility(View.VISIBLE);
                                break;
                            case -1:
                                if (getContext() != null){
                                    Toast.makeText(getContext(),
                                                data.getMessage(),
                                                Toast.LENGTH_LONG)
                                            .show();
                                }
                                textAHI.setText("?");
                                textMinSpO2.setText("?");
                                textSeverity.setText("?");
                                textMaxBPM.setText("?");
                                textMinBPM.setText("?");
                                textAvgBPM.setText("?");
                                textDiagnosis.setText("?");
                            case 100:
                                progressBar.setVisibility(View.INVISIBLE);
                                break;

                        }

                    }
                });

        OximeterData[] data = ((Store<OximeterData>)executionManager.getStore(KEY_DATA_INPUT))
                                    .retrieveLastN(MAX_DATA);

        List<DataPoint> dataBPM = new ArrayList<>();
        List<DataPoint> dataSpO2 = new ArrayList<>();

        for (OximeterData d:data){
            double x = (d.getId()-time)/1000d;
            if (d.getBPM() != -1)
                dataBPM.add(new DataPoint(x, d.getBPM()));
            if (d.getSpO2() != 127)
                dataSpO2.add(new DataPoint(x, d.getSpO2()));
        }

        seriesBPM.resetData(dataBPM.toArray(new DataPoint[dataBPM.size()]));
        seriesSpO2.resetData(dataSpO2.toArray(new DataPoint[dataSpO2.size()]));
    }

}
