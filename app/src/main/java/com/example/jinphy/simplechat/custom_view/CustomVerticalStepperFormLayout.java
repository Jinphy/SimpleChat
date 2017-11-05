package com.example.jinphy.simplechat.custom_view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.widget.LinearLayout;

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

    /**
     * 重写该函数设置 最后一步的注册按钮的文本
     * */
    @Override
    protected void setUpStepLayoutAsConfirmationStepLayout(LinearLayout stepLayout) {
        super.setUpStepLayoutAsConfirmationStepLayout(stepLayout);

        confirmationButton = stepLayout.findViewById(ernestoyaquello.com
                .verticalstepperform.R.id.next_step);
        confirmationButton.setText(R.string.confirm);
    }

    /**
     * 重写该函数设置最后一步的title
     * */
    @Override
    protected void addConfirmationStepToStepsList() {
        String confirmationStepText = context.getString(R.string.commit_data);
        steps.add(confirmationStepText);
    }

    /**
     * 重写该函数设置每一步的下一步按钮的文本 “下一步”
     * */
    @Override
    protected LinearLayout createStepLayout(final int stepNumber) {
        LinearLayout stepLayout = super.createStepLayout(stepNumber);

        AppCompatButton nextButton = stepLayout.findViewById(ernestoyaquello
                .com.verticalstepperform.R.id.next_step);
        nextButton.setText(R.string.next_step);
        return stepLayout;
    }
}
