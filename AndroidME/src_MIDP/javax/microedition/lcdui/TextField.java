package javax.microedition.lcdui;


import android.content.Context;
import android.graphics.Typeface;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.TextView;

public class TextField {
    public static final int ANY             = 0x000;
    public static final int DECIMAL         = 0x001;
    public static final int EMAILADDR       = 0x002;
    public static final int NUMERIC         = 0x020;
    public static final int PHONENUMBER     = 0x080;
    public static final int URL             = 0x200;
    public static final int CONSTRAINT_MASK = 0xFFFF;

    public static final int PASSWORD 				= 0x10000;
    public static final int UNEDITABLE 				= 0x20000;
    public static final int SENSITIVE 				= 0x40000;
    public static final int NON_PREDICTIVE 			= 0x80000;
    public static final int INITIAL_CAPS_WORD 		= 0x100000;
    public static final int INITIAL_CAPS_SENTENCE 	= 0x200000;



	public static TextView createTextView(int constraints, Context context) {
        boolean isEditable = (constraints & TextField.UNEDITABLE ) == 0;
        TextView textView;
		if (isEditable) {
 //JP           MovementMethod movementMethod = new ArrowKeyMovementMethod();
 //Jp           TransformationMethod transformationMethod;
// input method appears to have been scrapped?
//            InputMethod inputMethod;
//            if( ( constraints & TextField.NUMERIC ) > 0 )
//            {
//                inputMethod = new DigitsInputMethod();
//            }
//            else if( ( constraints & TextField.PHONENUMBER ) > 0 )
//            {
//                inputMethod = new DialerInputMethod();
//            }
//            else
//            {
//                Capitalize capitalize;
//                // assume text
//                if( ( constraints & TextField.INITIAL_CAPS_SENTENCE ) > 0 )
//                {
//                    capitalize = Capitalize.SENTENCES;
//                }
//                else if( ( constraints & TextField.INITIAL_CAPS_WORD ) > 0 )
//                {
//                    capitalize = Capitalize.WORDS;
//                }
//                else
//                {
//                    capitalize = Capitalize.NONE;
//                }
//                boolean autotext = ( constraints & TextField.NON_PREDICTIVE ) == 0;
//                inputMethod = new TextInputMethod( capitalize, autotext );
//            }


			EditText editText = new EditText(context);

			if ((constraints & TextField.PASSWORD) > 0) {
				editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
				editText.setTypeface(Typeface.MONOSPACE);
			}

			if ((constraints & TextField.URL) != 0) {
				editText.setSingleLine(true);
			}


//JP            editText.setMovementMethod( movementMethod );
            //editText.setInputMethod( inputMethod );
//JP            editText.setTransformationMethod( transformationMethod );

         // JP InputType
         //   editText.setInputType(InputType.TYPE_CLASS_PHONE)


            textView = editText;
		} else {
			// ignore the other constraints
			textView = new TextView(context);
		}
        return textView;
    }

	public TextField(String label, String text, int maxSize, int constraints) {
		throw new RuntimeException("TextField: Not implemented.");
	}
}
