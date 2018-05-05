package com.test.bank.view.activity;

import android.graphics.Canvas;

import com.test.bank.R;
import com.test.bank.base.BaseLocalDataActivity;
import com.test.bank.utils.LogUtils;
import com.lidong.pdf.PDFView;
import com.lidong.pdf.listener.OnDrawListener;
import com.lidong.pdf.listener.OnLoadCompleteListener;
import com.lidong.pdf.listener.OnPageChangeListener;

import butterknife.BindView;

public class PDFViewerActivity extends BaseLocalDataActivity implements OnPageChangeListener, OnLoadCompleteListener, OnDrawListener {
    @BindView(R.id.pdfView_net_new)
    PDFView pdfViewNet;//网络

    public static final String KEY_OF_PDF_URL = "pdfUrlKey";
    public static final String KEY_OF_TITLE = "titleKey";
    private String title = "";

    @Override
    protected String getPageTitle() {
        return "";
    }

    @Override
    protected int getSubLayoutId() {
        return R.layout.activity_pdf_viewer;
    }

    @Override
    protected void initPageData() {
        String pdfUrl = getIntent().getStringExtra(KEY_OF_PDF_URL);
        title = getIntent().getStringExtra(KEY_OF_TITLE);
        baseTitleTextView.setText(title);

        displayFromFile(pdfUrl, pdfUrl.substring(pdfUrl.lastIndexOf("/")));
    }

    private void displayFromFile(String fileUrl, String fileName) {
        pdfViewNet.fileFromLocalStorage(this, this, this, fileUrl, fileName);   //设置pdf文件地址
    }


    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
        LogUtils.printLog("");
    }

    @Override
    public void loadComplete(int nbPages) {
        LogUtils.printLog("");
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        LogUtils.printLog("");
    }
}