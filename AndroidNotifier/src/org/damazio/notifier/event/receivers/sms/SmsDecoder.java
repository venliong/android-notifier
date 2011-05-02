package org.damazio.notifier.event.receivers.sms;

import static org.damazio.notifier.Constants.TAG;

import org.damazio.notifier.R;
import org.damazio.notifier.event.util.PhoneNumberUtils;
import org.damazio.notifier.protocol.Common.PhoneNumber;
import org.damazio.notifier.protocol.Notifications.SmsNotification;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Class which handles decoding SMS messages in the proper way depending on the
 * version of Android being run.
 *
 * @author Rodrigo Damazio
 */
class SmsDecoder {
  private final Context context;
  private final PhoneNumberUtils numberUtils;

  public SmsDecoder(Context context, PhoneNumberUtils numberUtils) {
    this.context = context;
    this.numberUtils = numberUtils;
  }

  public SmsNotification decodeSms(Object pdu) {
    SmsMessage message = null;
    try {
      message = SmsMessage.createFromPdu((byte[]) pdu);
    } catch (NullPointerException e) {
      // Workaround for Android bug
      // http://code.google.com/p/android/issues/detail?id=11345
      Log.w(TAG, "Invalid PDU", e);
      return null;
    }

    PhoneNumber number = numberUtils.resolvePhoneNumber(message.getOriginatingAddress());
    String body = message.getMessageBody();
    if (body == null) {
      body = context.getString(R.string.sms_body_unavailable);
    }

    return SmsNotification.newBuilder()
        .setSender(number)
        .setText(body)
        .build();
  }


}
