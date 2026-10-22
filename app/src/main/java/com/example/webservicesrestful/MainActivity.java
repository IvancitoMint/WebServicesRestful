package com.example.webservicesrestful;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.webservicesrestful.api.ApiService;
import com.example.webservicesrestful.api.RetrofitClient;
import com.example.webservicesrestful.model.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ApiService apiService;
    private ProgressBar progressBar;
    private TextView textResults;
    private Button buttonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.main);
        int horizontalPadding = root.getPaddingLeft();
        int verticalPadding = root.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(horizontalPadding + systemBars.left,
                    verticalPadding + systemBars.top,
                    horizontalPadding + systemBars.right,
                    verticalPadding + systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getApiService();
        progressBar = findViewById(R.id.progressBar);
        textResults = findViewById(R.id.textResults);
        buttonCreate = findViewById(R.id.buttonCreate);

        buttonCreate.setOnClickListener(view -> createReportOnServer());
        fetchReportsFromServer();
    }

    private void fetchReportsFromServer() {
        setLoading(true);
        apiService.getAllReports().enqueue(new Callback<List<Report>>() {
            @Override
            public void onResponse(Call<List<Report>> call, Response<List<Report>> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Report> reports = response.body();
                    StringBuilder result = new StringBuilder();
                    int itemsToShow = Math.min(reports.size(), 10);
                    for (int i = 0; i < itemsToShow; i++) {
                        Report report = reports.get(i);
                        result.append("#").append(report.getId())
                                .append(" — ").append(report.getTitle())
                                .append("\n\n");
                    }
                    textResults.setText(result.toString());
                    Log.d("API_SUCCESS", "Retrieved " + reports.size() + " records.");
                    Toast.makeText(MainActivity.this,
                            getString(R.string.loaded_items, reports.size()),
                            Toast.LENGTH_SHORT).show();
                } else {
                    showError(getString(R.string.server_error, response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Report>> call, Throwable throwable) {
                setLoading(false);
                showError(getString(R.string.network_failure));
            }
        });
    }

    private void createReportOnServer() {
        setLoading(true);
        Report newReport = new Report(1, "Mobile laboratory report",
                "Created asynchronously from the Android client");

        apiService.createReport(newReport).enqueue(new Callback<Report>() {
            @Override
            public void onResponse(Call<Report> call, Response<Report> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    String message = getString(R.string.created_entity, response.body().getId());
                    Log.d("API_POST_SUCCESS", message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                } else {
                    showError(getString(R.string.post_server_error, response.code()));
                }
            }

            @Override
            public void onFailure(Call<Report> call, Throwable throwable) {
                setLoading(false);
                showError(getString(R.string.post_network_failure));
            }
        });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonCreate.setEnabled(!loading);
    }

    private void showError(String message) {
        Log.e("API_ERROR", message);
        textResults.setText(message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
