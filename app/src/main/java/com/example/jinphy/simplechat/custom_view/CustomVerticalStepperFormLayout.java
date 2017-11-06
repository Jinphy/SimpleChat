package com.example.jinphy.simplechat.custom_view;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jinphy.simplechat.R;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormLayout;

/**
 * Created by jinphy on 2017/11/4.
 */

public class CustomVerticalStepperFormLayout extends VerticalStepperFormLayout {

    public CustomVerticalStepperFormLayout(Context context) {
        super(context);
    }

    public CustomVerticalStepperFormLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVerticalStepperFormLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    /**
//     * 重写该函数设置 最后一步的注册按钮的文本
//     * */
//    @Override
//    protected void setUpStepLayoutAsConfirmationStepLayout(LinearLayout stepLayout) {
//        super.setUpStepLayoutAsConfirmationStepLayout(stepLayout);
//
//        confirmationButton = stepLayout.findViewById(ernestoyaquello.com
//                .verticalstepperform.R.id.next_step);
//
//        confirmationButton.setText(R.string.confirm);
//    }

//
//    /**
//     * 重写该函数设置最后一步的title
//     * */
//    @Override
//    protected void addConfirmationStepToStepsList() {
//        String confirmationStepText = context.getString(R.string.commit_data);
//        steps.add(confirmationStepText);
//    }

//    /**
//     * 重写该函数设置每一步的下一步按钮的文本 “下一步”
//     * */
//    @Override
//    protected LinearLayout createStepLayout(final int stepNumber) {
//        LinearLayout stepLayout = super.createStepLayout(stepNumber);
//
//        AppCompatButton nextButton = stepLayout.findViewById(ernestoyaquello
//                .com.verticalstepperform.R.id.next_step);
//        nextButton.setText(R.string.next_step);
//        return stepLayout;
//    }

    public CustomVerticalStepperFormLayout setNextStepButtonText(int stepNumber, String text) {
        getNextStepButton(stepNumber).setText(text);
        return this;
    }

    public CustomVerticalStepperFormLayout setNextStepButtonText(int stepNumber, @StringRes int resourceId) {
        getNextStepButton(stepNumber).setText(resourceId);
        return this;
    }

    public CustomVerticalStepperFormLayout setNextStepButtonTexts(String text) {
        for (int i = 0; i < numberOfSteps; i++) {
            getNextStepButton(i).setText(text);
        }
        return this;
    }

    public CustomVerticalStepperFormLayout setNextStepButtonTexts(@StringRes int resourceId) {
        for (int i = 0; i < numberOfSteps; i++) {
            getNextStepButton(i).setText(resourceId);
        }
        return this;
    }


    public CustomVerticalStepperFormLayout setConfirmStepTitle(String title) {
        setStepTitle(numberOfSteps, title);
        return this;
    }
    public CustomVerticalStepperFormLayout setButtonTextOfConfirmStep(String text){
        getNextStepButton(numberOfSteps).setText(text);
        return this;
    }


    public CustomVerticalStepperFormLayout setButtonTextOfConfirmStep(@StringRes int resourceId){
        getNextStepButton(numberOfSteps).setText(resourceId);
        return this;
    }

    public AppCompatButton getNextStepButton(int stepNumber) {

        LinearLayout stepLayout = (LinearLayout) content.getChildAt(stepNumber);

        return stepLayout.findViewById(ernestoyaquello.com.verticalstepperform.R.id.next_step);
    }

    public TextView getStepTitleView(int stepNumber) {
        return stepsTitlesViews.get(stepNumber);
    }

    public AppCompatButton getConfirmStepButton() {
        return getNextStepButton(numberOfSteps);
    }

    public int getSteps(){
        return numberOfSteps;
    }

    public CustomVerticalStepperFormLayout hideScrollBar(){
        stepsScrollView.setVerticalScrollBarEnabled(false);
        return this;
    }


    public void next(int currentStepNumber) {
        goToStep((currentStepNumber + 1), false);
    }
}
