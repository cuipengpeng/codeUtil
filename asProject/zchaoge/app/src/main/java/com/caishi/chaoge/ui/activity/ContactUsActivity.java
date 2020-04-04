//package com.caishi.chaoge.ui.activity;
//
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//import android.widget.TextView;
//
//import com.caishi.chaoge.R;
//import com.caishi.chaoge.base.BaseActivity;
//import com.caishi.chaoge.bean.ContactUsBean;
//import com.caishi.chaoge.ui.widget.dialog.DialogUtil;
//import com.caishi.chaoge.ui.widget.dialog.IDialog;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.BaseViewHolder;
//import com.gyf.barlibrary.ImmersionBar;
//
//import java.util.ArrayList;
//
//public class ContactUsActivity extends BaseActivity {
//
//
//    private RecyclerView rv_contactUs_list;
//    private ContactUsAdapter contactUsAdapter;
//    String[] title = new String[]{"客服电话", "客服邮箱", "QQ群", "微信号"};
//    String[] info = new String[]{"010-57721839", "chaogegf@chaogevideo.com", "929059743", "chaoge_018"};
//    private ArrayList<ContactUsBean> contactUsList;
//
//
//    @Override
//    public void initBundle(Bundle bundle) {
//
//    }
//
//    @Override
//    public int bindLayout() {
//        return R.layout.activity_contact_us;
//    }
//
//    @Override
//    public void initView(View view) {
//        contactUsList = new ArrayList<>();
//        ImmersionBar.with(this).transparentStatusBar().titleBar(R.id.view_contactUs)
//                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
//                .init();
//        $(R.id.ll_baseTitle_back).setOnClickListener(this);
//        ((TextView) $(R.id.tv_baseTitle_title)).setText("联系朝歌");
//        rv_contactUs_list = $(R.id.rv_contactUs_list);
//        for (int i = 0; i < title.length; i++) {
//            ContactUsBean contactUsBean = new ContactUsBean();
//            contactUsBean.title = title[i];
//            contactUsBean.info = info[i];
//            contactUsList.add(contactUsBean);
//
//        }
//
//
//    }
//
//    @Override
//    public void setListener() {
////        contactUsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
////            @Override
////            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
////                if (position == 0) {
////
////
////                }
////
////            }
////        });
//    }
//
//    @Override
//    public void doBusiness() {
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        rv_contactUs_list.setLayoutManager(linearLayoutManager);
//        contactUsAdapter = new ContactUsAdapter();
//        rv_contactUs_list.setAdapter(contactUsAdapter);
//        contactUsAdapter.setNewData(contactUsList);
//        contactUsAdapter.bindToRecyclerView(rv_contactUs_list);
//
//
//    }
//
//    @Override
//    public void widgetClick(View v) {
//        switch (v.getId()) {
//            case R.id.ll_baseTitle_back:
//                finish();
//                break;
//
//        }
//
//    }
//
//    /**
//     * 字体的adapter
//     */
//    class ContactUsAdapter extends BaseQuickAdapter<ContactUsBean, BaseViewHolder> {
//
//        public ContactUsAdapter() {
//            super(R.layout.item_contact_us);
//        }
//
//        @Override
//        protected void convert(final BaseViewHolder helper, ContactUsBean item) {
//            final TextView tv_contactUs_info = helper.getView(R.id.tv_contactUs_info);
//            tv_contactUs_info.setText(item.info);
//
//            tv_contactUs_info.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (helper.getAdapterPosition() == 0) {
//                        DialogUtil.createDefaultDialog(mContext, "客服电话", "010-57721839",
//                                "立即呼叫", new IDialog.OnClickListener() {
//                                    @Override
//                                    public void onClick(IDialog dialog) {
////                                                        Intent intent = new Intent(Intent.ACTION_CALL);
////                                                        Uri data = Uri.parse("tel:" + "010-57721839");
////                                                        intent.setData(data);
////                                                        startActivity(intent);
//                                        Intent intent = new Intent(Intent.ACTION_DIAL);
//                                        Uri data = Uri.parse("tel:" + "010-57721839");
//                                        intent.setData(data);
//                                        startActivity(intent);
//                                        dialog.dismiss();
//                                    }
//                                },
//                                "取消", new IDialog.OnClickListener() {
//                                    @Override
//                                    public void onClick(IDialog dialog) {
//                                        dialog.dismiss();
//                                    }
//                                });
//
//
//                    }else {
//
//
//                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//                        // 将文本内容放到系统剪贴板里。
//                        ClipData mClipData = ClipData.newPlainText("Label", tv_contactUs_info.getText().toString());
//                        if (cm != null) {
//                            cm.setPrimaryClip(mClipData);
//                            showToast("已复制到粘贴板");
//                        }
//                    }
//                }
//            });
//
//            helper.setText(R.id.tv_contactUs_title, item.title);
//
//
//        }
//    }
//
//
//}
