package dev.buildtool.json;

import java.util.*;
import java.util.regex.Pattern;

/**
 * A parser translated from the original Javascript parser.
 */
public class Json5Parser {
    public static final String SINGLE_LINE_COMMENT = "singleLineComment";
    public static final String MULTI_LINE_COMMENT = "multiLineComment";
    public static final String MULTI_LINE_COMMENT_ASTERISK = "multiLineCommentAsterisk";
    public static final String PUNCTUATOR = "punctuator";
    public static final String SIGN = "sign";
    public static final String BOOLEAN = "boolean";
    public static final String NUMERIC = "numeric";
    public static final String IDENTIFIER = "identifier";
    public static final String IDENTIFIER_NAME_ESCAPE = "identifierNameEscape";
    public static final String IDENTIFIER_NAME = "identifierName";
    public static final String STRING = "string";
    public static final String ZERO = "zero";
    public static final String DECIMAL_INTEGER = "decimalInteger";
    public static final String DECIMAL_POINT_LEADING = "decimalPointLeading";
    public static final String DECIMAL_POINT = "decimalPoint";
    public static final String DECIMAL_EXPONENT = "decimalExponent";
    public static final String HEXADECIMAL = "hexadecimal";
    public static final String DECIMAL_FRACTION = "decimalFraction";
    public static final String DECIMAL_EXPONENT_SIGN = "decimalExponentSign";
    public static final String DECIMAL_EXPONENT_INTEGER = "decimalExponentInteger";
    public static final String HEXADECIMAL_INTEGER = "hexadecimalInteger";
    public static final String VALUE = "value";
    public static final String AFTER_ARRAY_VALUE = "afterArrayValue";
    public static final String AFTER_PROPERTY_VALUE = "afterPropertyValue";
    public static final String BEFORE_PROPERTY_NAME = "beforePropertyName";
    public static final String AFTER_PROPERTY_NAME = "afterPropertyName";
    public static final String BEFORE_PROPERTY_VALUE = "beforePropertyValue";
    public static final String COMMENT = "comment";
    public static final String BEFORE_ARRAY_VALUE = "beforeArrayValue";
    public static final String IDENTIFIER_NAME_START_ESCAPE = "identifierNameStartEscape";
    public static final String END = "end";
    String source;
    String parseState;
    List<Object> stack;
    int pos;
    int line;
    int column;
    Token token;
    Object key;
    Object root;

    final static Pattern SPACE_SEPARATOR = Pattern.compile("/[\u1680\u2000-\u200A\u202F\u205F\u3000]/");
    final static Pattern ID_START = Pattern.compile("[\\xAA\\xB5\\xBA\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\u02C1\\u02C6-\\u02D1\\u02E0-\\u02E4\\u02EC\\u02EE\\u0370-\\u0374\\u0376\\u0377\\u037A-\\u037D\\u037F\\u0386\\u0388-\\u038A\\u038C\\u038E-\\u03A1\\u03A3-\\u03F5\\u03F7-\\u0481\\u048A-\\u052F\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u05D0-\\u05EA\\u05F0-\\u05F2\\u0620-\\u064A\\u066E\\u066F\\u0671-\\u06D3\\u06D5\\u06E5\\u06E6\\u06EE\\u06EF\\u06FA-\\u06FC\\u06FF\\u0710\\u0712-\\u072F\\u074D-\\u07A5\\u07B1\\u07CA-\\u07EA\\u07F4\\u07F5\\u07FA\\u0800-\\u0815\\u081A\\u0824\\u0828\\u0840-\\u0858\\u0860-\\u086A\\u08A0-\\u08B4\\u08B6-\\u08BD\\u0904-\\u0939\\u093D\\u0950\\u0958-\\u0961\\u0971-\\u0980\\u0985-\\u098C\\u098F\\u0990\\u0993-\\u09A8\\u09AA-\\u09B0\\u09B2\\u09B6-\\u09B9\\u09BD\\u09CE\\u09DC\\u09DD\\u09DF-\\u09E1\\u09F0\\u09F1\\u09FC\\u0A05-\\u0A0A\\u0A0F\\u0A10\\u0A13-\\u0A28\\u0A2A-\\u0A30\\u0A32\\u0A33\\u0A35\\u0A36\\u0A38\\u0A39\\u0A59-\\u0A5C\\u0A5E\\u0A72-\\u0A74\\u0A85-\\u0A8D\\u0A8F-\\u0A91\\u0A93-\\u0AA8\\u0AAA-\\u0AB0\\u0AB2\\u0AB3\\u0AB5-\\u0AB9\\u0ABD\\u0AD0\\u0AE0\\u0AE1\\u0AF9\\u0B05-\\u0B0C\\u0B0F\\u0B10\\u0B13-\\u0B28\\u0B2A-\\u0B30\\u0B32\\u0B33\\u0B35-\\u0B39\\u0B3D\\u0B5C\\u0B5D\\u0B5F-\\u0B61\\u0B71\\u0B83\\u0B85-\\u0B8A\\u0B8E-\\u0B90\\u0B92-\\u0B95\\u0B99\\u0B9A\\u0B9C\\u0B9E\\u0B9F\\u0BA3\\u0BA4\\u0BA8-\\u0BAA\\u0BAE-\\u0BB9\\u0BD0\\u0C05-\\u0C0C\\u0C0E-\\u0C10\\u0C12-\\u0C28\\u0C2A-\\u0C39\\u0C3D\\u0C58-\\u0C5A\\u0C60\\u0C61\\u0C80\\u0C85-\\u0C8C\\u0C8E-\\u0C90\\u0C92-\\u0CA8\\u0CAA-\\u0CB3\\u0CB5-\\u0CB9\\u0CBD\\u0CDE\\u0CE0\\u0CE1\\u0CF1\\u0CF2\\u0D05-\\u0D0C\\u0D0E-\\u0D10\\u0D12-\\u0D3A\\u0D3D\\u0D4E\\u0D54-\\u0D56\\u0D5F-\\u0D61\\u0D7A-\\u0D7F\\u0D85-\\u0D96\\u0D9A-\\u0DB1\\u0DB3-\\u0DBB\\u0DBD\\u0DC0-\\u0DC6\\u0E01-\\u0E30\\u0E32\\u0E33\\u0E40-\\u0E46\\u0E81\\u0E82\\u0E84\\u0E87\\u0E88\\u0E8A\\u0E8D\\u0E94-\\u0E97\\u0E99-\\u0E9F\\u0EA1-\\u0EA3\\u0EA5\\u0EA7\\u0EAA\\u0EAB\\u0EAD-\\u0EB0\\u0EB2\\u0EB3\\u0EBD\\u0EC0-\\u0EC4\\u0EC6\\u0EDC-\\u0EDF\\u0F00\\u0F40-\\u0F47\\u0F49-\\u0F6C\\u0F88-\\u0F8C\\u1000-\\u102A\\u103F\\u1050-\\u1055\\u105A-\\u105D\\u1061\\u1065\\u1066\\u106E-\\u1070\\u1075-\\u1081\\u108E\\u10A0-\\u10C5\\u10C7\\u10CD\\u10D0-\\u10FA\\u10FC-\\u1248\\u124A-\\u124D\\u1250-\\u1256\\u1258\\u125A-\\u125D\\u1260-\\u1288\\u128A-\\u128D\\u1290-\\u12B0\\u12B2-\\u12B5\\u12B8-\\u12BE\\u12C0\\u12C2-\\u12C5\\u12C8-\\u12D6\\u12D8-\\u1310\\u1312-\\u1315\\u1318-\\u135A\\u1380-\\u138F\\u13A0-\\u13F5\\u13F8-\\u13FD\\u1401-\\u166C\\u166F-\\u167F\\u1681-\\u169A\\u16A0-\\u16EA\\u16EE-\\u16F8\\u1700-\\u170C\\u170E-\\u1711\\u1720-\\u1731\\u1740-\\u1751\\u1760-\\u176C\\u176E-\\u1770\\u1780-\\u17B3\\u17D7\\u17DC\\u1820-\\u1877\\u1880-\\u1884\\u1887-\\u18A8\\u18AA\\u18B0-\\u18F5\\u1900-\\u191E\\u1950-\\u196D\\u1970-\\u1974\\u1980-\\u19AB\\u19B0-\\u19C9\\u1A00-\\u1A16\\u1A20-\\u1A54\\u1AA7\\u1B05-\\u1B33\\u1B45-\\u1B4B\\u1B83-\\u1BA0\\u1BAE\\u1BAF\\u1BBA-\\u1BE5\\u1C00-\\u1C23\\u1C4D-\\u1C4F\\u1C5A-\\u1C7D\\u1C80-\\u1C88\\u1CE9-\\u1CEC\\u1CEE-\\u1CF1\\u1CF5\\u1CF6\\u1D00-\\u1DBF\\u1E00-\\u1F15\\u1F18-\\u1F1D\\u1F20-\\u1F45\\u1F48-\\u1F4D\\u1F50-\\u1F57\\u1F59\\u1F5B\\u1F5D\\u1F5F-\\u1F7D\\u1F80-\\u1FB4\\u1FB6-\\u1FBC\\u1FBE\\u1FC2-\\u1FC4\\u1FC6-\\u1FCC\\u1FD0-\\u1FD3\\u1FD6-\\u1FDB\\u1FE0-\\u1FEC\\u1FF2-\\u1FF4\\u1FF6-\\u1FFC\\u2071\\u207F\\u2090-\\u209C\\u2102\\u2107\\u210A-\\u2113\\u2115\\u2119-\\u211D\\u2124\\u2126\\u2128\\u212A-\\u212D\\u212F-\\u2139\\u213C-\\u213F\\u2145-\\u2149\\u214E\\u2160-\\u2188\\u2C00-\\u2C2E\\u2C30-\\u2C5E\\u2C60-\\u2CE4\\u2CEB-\\u2CEE\\u2CF2\\u2CF3\\u2D00-\\u2D25\\u2D27\\u2D2D\\u2D30-\\u2D67\\u2D6F\\u2D80-\\u2D96\\u2DA0-\\u2DA6\\u2DA8-\\u2DAE\\u2DB0-\\u2DB6\\u2DB8-\\u2DBE\\u2DC0-\\u2DC6\\u2DC8-\\u2DCE\\u2DD0-\\u2DD6\\u2DD8-\\u2DDE\\u2E2F\\u3005-\\u3007\\u3021-\\u3029\\u3031-\\u3035\\u3038-\\u303C\\u3041-\\u3096\\u309D-\\u309F\\u30A1-\\u30FA\\u30FC-\\u30FF\\u3105-\\u312E\\u3131-\\u318E\\u31A0-\\u31BA\\u31F0-\\u31FF\\u3400-\\u4DB5\\u4E00-\\u9FEA\\uA000-\\uA48C\\uA4D0-\\uA4FD\\uA500-\\uA60C\\uA610-\\uA61F\\uA62A\\uA62B\\uA640-\\uA66E\\uA67F-\\uA69D\\uA6A0-\\uA6EF\\uA717-\\uA71F\\uA722-\\uA788\\uA78B-\\uA7AE\\uA7B0-\\uA7B7\\uA7F7-\\uA801\\uA803-\\uA805\\uA807-\\uA80A\\uA80C-\\uA822\\uA840-\\uA873\\uA882-\\uA8B3\\uA8F2-\\uA8F7\\uA8FB\\uA8FD\\uA90A-\\uA925\\uA930-\\uA946\\uA960-\\uA97C\\uA984-\\uA9B2\\uA9CF\\uA9E0-\\uA9E4\\uA9E6-\\uA9EF\\uA9FA-\\uA9FE\\uAA00-\\uAA28\\uAA40-\\uAA42\\uAA44-\\uAA4B\\uAA60-\\uAA76\\uAA7A\\uAA7E-\\uAAAF\\uAAB1\\uAAB5\\uAAB6\\uAAB9-\\uAABD\\uAAC0\\uAAC2\\uAADB-\\uAADD\\uAAE0-\\uAAEA\\uAAF2-\\uAAF4\\uAB01-\\uAB06\\uAB09-\\uAB0E\\uAB11-\\uAB16\\uAB20-\\uAB26\\uAB28-\\uAB2E\\uAB30-\\uAB5A\\uAB5C-\\uAB65\\uAB70-\\uABE2\\uAC00-\\uD7A3\\uD7B0-\\uD7C6\\uD7CB-\\uD7FB\\uF900-\\uFA6D\\uFA70-\\uFAD9\\uFB00-\\uFB06\\uFB13-\\uFB17\\uFB1D\\uFB1F-\\uFB28\\uFB2A-\\uFB36\\uFB38-\\uFB3C\\uFB3E\\uFB40\\uFB41\\uFB43\\uFB44\\uFB46-\\uFBB1\\uFBD3-\\uFD3D\\uFD50-\\uFD8F\\uFD92-\\uFDC7\\uFDF0-\\uFDFB\\uFE70-\\uFE74\\uFE76-\\uFEFC\\uFF21-\\uFF3A\\uFF41-\\uFF5A\\uFF66-\\uFFBE\\uFFC2-\\uFFC7\\uFFCA-\\uFFCF\\uFFD2-\\uFFD7\\uFFDA-\\uFFDC]");
    final static Pattern ID_CONTINUE = Pattern.compile("[\\xAA\\xB5\\xBA\\xC0-\\xD6\\xD8-\\xF6\\xF8-\\u02C1\\u02C6-\\u02D1\\u02E0-\\u02E4\\u02EC\\u02EE\\u0300-\\u0374\\u0376\\u0377\\u037A-\\u037D\\u037F\\u0386\\u0388-\\u038A\\u038C\\u038E-\\u03A1\\u03A3-\\u03F5\\u03F7-\\u0481\\u0483-\\u0487\\u048A-\\u052F\\u0531-\\u0556\\u0559\\u0561-\\u0587\\u0591-\\u05BD\\u05BF\\u05C1\\u05C2\\u05C4\\u05C5\\u05C7\\u05D0-\\u05EA\\u05F0-\\u05F2\\u0610-\\u061A\\u0620-\\u0669\\u066E-\\u06D3\\u06D5-\\u06DC\\u06DF-\\u06E8\\u06EA-\\u06FC\\u06FF\\u0710-\\u074A\\u074D-\\u07B1\\u07C0-\\u07F5\\u07FA\\u0800-\\u082D\\u0840-\\u085B\\u0860-\\u086A\\u08A0-\\u08B4\\u08B6-\\u08BD\\u08D4-\\u08E1\\u08E3-\\u0963\\u0966-\\u096F\\u0971-\\u0983\\u0985-\\u098C\\u098F\\u0990\\u0993-\\u09A8\\u09AA-\\u09B0\\u09B2\\u09B6-\\u09B9\\u09BC-\\u09C4\\u09C7\\u09C8\\u09CB-\\u09CE\\u09D7\\u09DC\\u09DD\\u09DF-\\u09E3\\u09E6-\\u09F1\\u09FC\\u0A01-\\u0A03\\u0A05-\\u0A0A\\u0A0F\\u0A10\\u0A13-\\u0A28\\u0A2A-\\u0A30\\u0A32\\u0A33\\u0A35\\u0A36\\u0A38\\u0A39\\u0A3C\\u0A3E-\\u0A42\\u0A47\\u0A48\\u0A4B-\\u0A4D\\u0A51\\u0A59-\\u0A5C\\u0A5E\\u0A66-\\u0A75\\u0A81-\\u0A83\\u0A85-\\u0A8D\\u0A8F-\\u0A91\\u0A93-\\u0AA8\\u0AAA-\\u0AB0\\u0AB2\\u0AB3\\u0AB5-\\u0AB9\\u0ABC-\\u0AC5\\u0AC7-\\u0AC9\\u0ACB-\\u0ACD\\u0AD0\\u0AE0-\\u0AE3\\u0AE6-\\u0AEF\\u0AF9-\\u0AFF\\u0B01-\\u0B03\\u0B05-\\u0B0C\\u0B0F\\u0B10\\u0B13-\\u0B28\\u0B2A-\\u0B30\\u0B32\\u0B33\\u0B35-\\u0B39\\u0B3C-\\u0B44\\u0B47\\u0B48\\u0B4B-\\u0B4D\\u0B56\\u0B57\\u0B5C\\u0B5D\\u0B5F-\\u0B63\\u0B66-\\u0B6F\\u0B71\\u0B82\\u0B83\\u0B85-\\u0B8A\\u0B8E-\\u0B90\\u0B92-\\u0B95\\u0B99\\u0B9A\\u0B9C\\u0B9E\\u0B9F\\u0BA3\\u0BA4\\u0BA8-\\u0BAA\\u0BAE-\\u0BB9\\u0BBE-\\u0BC2\\u0BC6-\\u0BC8\\u0BCA-\\u0BCD\\u0BD0\\u0BD7\\u0BE6-\\u0BEF\\u0C00-\\u0C03\\u0C05-\\u0C0C\\u0C0E-\\u0C10\\u0C12-\\u0C28\\u0C2A-\\u0C39\\u0C3D-\\u0C44\\u0C46-\\u0C48\\u0C4A-\\u0C4D\\u0C55\\u0C56\\u0C58-\\u0C5A\\u0C60-\\u0C63\\u0C66-\\u0C6F\\u0C80-\\u0C83\\u0C85-\\u0C8C\\u0C8E-\\u0C90\\u0C92-\\u0CA8\\u0CAA-\\u0CB3\\u0CB5-\\u0CB9\\u0CBC-\\u0CC4\\u0CC6-\\u0CC8\\u0CCA-\\u0CCD\\u0CD5\\u0CD6\\u0CDE\\u0CE0-\\u0CE3\\u0CE6-\\u0CEF\\u0CF1\\u0CF2\\u0D00-\\u0D03\\u0D05-\\u0D0C\\u0D0E-\\u0D10\\u0D12-\\u0D44\\u0D46-\\u0D48\\u0D4A-\\u0D4E\\u0D54-\\u0D57\\u0D5F-\\u0D63\\u0D66-\\u0D6F\\u0D7A-\\u0D7F\\u0D82\\u0D83\\u0D85-\\u0D96\\u0D9A-\\u0DB1\\u0DB3-\\u0DBB\\u0DBD\\u0DC0-\\u0DC6\\u0DCA\\u0DCF-\\u0DD4\\u0DD6\\u0DD8-\\u0DDF\\u0DE6-\\u0DEF\\u0DF2\\u0DF3\\u0E01-\\u0E3A\\u0E40-\\u0E4E\\u0E50-\\u0E59\\u0E81\\u0E82\\u0E84\\u0E87\\u0E88\\u0E8A\\u0E8D\\u0E94-\\u0E97\\u0E99-\\u0E9F\\u0EA1-\\u0EA3\\u0EA5\\u0EA7\\u0EAA\\u0EAB\\u0EAD-\\u0EB9\\u0EBB-\\u0EBD\\u0EC0-\\u0EC4\\u0EC6\\u0EC8-\\u0ECD\\u0ED0-\\u0ED9\\u0EDC-\\u0EDF\\u0F00\\u0F18\\u0F19\\u0F20-\\u0F29\\u0F35\\u0F37\\u0F39\\u0F3E-\\u0F47\\u0F49-\\u0F6C\\u0F71-\\u0F84\\u0F86-\\u0F97\\u0F99-\\u0FBC\\u0FC6\\u1000-\\u1049\\u1050-\\u109D\\u10A0-\\u10C5\\u10C7\\u10CD\\u10D0-\\u10FA\\u10FC-\\u1248\\u124A-\\u124D\\u1250-\\u1256\\u1258\\u125A-\\u125D\\u1260-\\u1288\\u128A-\\u128D\\u1290-\\u12B0\\u12B2-\\u12B5\\u12B8-\\u12BE\\u12C0\\u12C2-\\u12C5\\u12C8-\\u12D6\\u12D8-\\u1310\\u1312-\\u1315\\u1318-\\u135A\\u135D-\\u135F\\u1380-\\u138F\\u13A0-\\u13F5\\u13F8-\\u13FD\\u1401-\\u166C\\u166F-\\u167F\\u1681-\\u169A\\u16A0-\\u16EA\\u16EE-\\u16F8\\u1700-\\u170C\\u170E-\\u1714\\u1720-\\u1734\\u1740-\\u1753\\u1760-\\u176C\\u176E-\\u1770\\u1772\\u1773\\u1780-\\u17D3\\u17D7\\u17DC\\u17DD\\u17E0-\\u17E9\\u180B-\\u180D\\u1810-\\u1819\\u1820-\\u1877\\u1880-\\u18AA\\u18B0-\\u18F5\\u1900-\\u191E\\u1920-\\u192B\\u1930-\\u193B\\u1946-\\u196D\\u1970-\\u1974\\u1980-\\u19AB\\u19B0-\\u19C9\\u19D0-\\u19D9\\u1A00-\\u1A1B\\u1A20-\\u1A5E\\u1A60-\\u1A7C\\u1A7F-\\u1A89\\u1A90-\\u1A99\\u1AA7\\u1AB0-\\u1ABD\\u1B00-\\u1B4B\\u1B50-\\u1B59\\u1B6B-\\u1B73\\u1B80-\\u1BF3\\u1C00-\\u1C37\\u1C40-\\u1C49\\u1C4D-\\u1C7D\\u1C80-\\u1C88\\u1CD0-\\u1CD2\\u1CD4-\\u1CF9\\u1D00-\\u1DF9\\u1DFB-\\u1F15\\u1F18-\\u1F1D\\u1F20-\\u1F45\\u1F48-\\u1F4D\\u1F50-\\u1F57\\u1F59\\u1F5B\\u1F5D\\u1F5F-\\u1F7D\\u1F80-\\u1FB4\\u1FB6-\\u1FBC\\u1FBE\\u1FC2-\\u1FC4\\u1FC6-\\u1FCC\\u1FD0-\\u1FD3\\u1FD6-\\u1FDB\\u1FE0-\\u1FEC\\u1FF2-\\u1FF4\\u1FF6-\\u1FFC\\u203F\\u2040\\u2054\\u2071\\u207F\\u2090-\\u209C\\u20D0-\\u20DC\\u20E1\\u20E5-\\u20F0\\u2102\\u2107\\u210A-\\u2113\\u2115\\u2119-\\u211D\\u2124\\u2126\\u2128\\u212A-\\u212D\\u212F-\\u2139\\u213C-\\u213F\\u2145-\\u2149\\u214E\\u2160-\\u2188\\u2C00-\\u2C2E\\u2C30-\\u2C5E\\u2C60-\\u2CE4\\u2CEB-\\u2CF3\\u2D00-\\u2D25\\u2D27\\u2D2D\\u2D30-\\u2D67\\u2D6F\\u2D7F-\\u2D96\\u2DA0-\\u2DA6\\u2DA8-\\u2DAE\\u2DB0-\\u2DB6\\u2DB8-\\u2DBE\\u2DC0-\\u2DC6\\u2DC8-\\u2DCE\\u2DD0-\\u2DD6\\u2DD8-\\u2DDE\\u2DE0-\\u2DFF\\u2E2F\\u3005-\\u3007\\u3021-\\u302F\\u3031-\\u3035\\u3038-\\u303C\\u3041-\\u3096\\u3099\\u309A\\u309D-\\u309F\\u30A1-\\u30FA\\u30FC-\\u30FF\\u3105-\\u312E\\u3131-\\u318E\\u31A0-\\u31BA\\u31F0-\\u31FF\\u3400-\\u4DB5\\u4E00-\\u9FEA\\uA000-\\uA48C\\uA4D0-\\uA4FD\\uA500-\\uA60C\\uA610-\\uA62B\\uA640-\\uA66F\\uA674-\\uA67D\\uA67F-\\uA6F1\\uA717-\\uA71F\\uA722-\\uA788\\uA78B-\\uA7AE\\uA7B0-\\uA7B7\\uA7F7-\\uA827\\uA840-\\uA873\\uA880-\\uA8C5\\uA8D0-\\uA8D9\\uA8E0-\\uA8F7\\uA8FB\\uA8FD\\uA900-\\uA92D\\uA930-\\uA953\\uA960-\\uA97C\\uA980-\\uA9C0\\uA9CF-\\uA9D9\\uA9E0-\\uA9FE\\uAA00-\\uAA36\\uAA40-\\uAA4D\\uAA50-\\uAA59\\uAA60-\\uAA76\\uAA7A-\\uAAC2\\uAADB-\\uAADD\\uAAE0-\\uAAEF\\uAAF2-\\uAAF6\\uAB01-\\uAB06\\uAB09-\\uAB0E\\uAB11-\\uAB16\\uAB20-\\uAB26\\uAB28-\\uAB2E\\uAB30-\\uAB5A\\uAB5C-\\uAB65\\uAB70-\\uABEA\\uABEC\\uABED\\uABF0-\\uABF9\\uAC00-\\uD7A3\\uD7B0-\\uD7C6\\uD7CB-\\uD7FB\\uF900-\\uFA6D\\uFA70-\\uFAD9\\uFB00-\\uFB06\\uFB13-\\uFB17\\uFB1D-\\uFB28\\uFB2A-\\uFB36\\uFB38-\\uFB3C\\uFB3E\\uFB40\\uFB41\\uFB43\\uFB44\\uFB46-\\uFBB1\\uFBD3-\\uFD3D\\uFD50-\\uFD8F\\uFD92-\\uFDC7\\uFDF0-\\uFDFB\\uFE00-\\uFE0F\\uFE20-\\uFE2F\\uFE33\\uFE34\\uFE4D-\\uFE4F\\uFE70-\\uFE74\\uFE76-\\uFEFC\\uFF10-\\uFF19\\uFF21-\\uFF3A\\uFF3F\\uFF41-\\uFF5A\\uFF66-\\uFFBE\\uFFC2-\\uFFC7\\uFFCA-\\uFFCF\\uFFD2-\\uFFD7\\uFFDA-\\uFFDC]");
    final String START = "start", EOF = "eof", DEFAULT = "default";

    /**
     * @param object Json string
     */
    public Json5Parser(String object) {
        source = object;
    }

    boolean isSpaceSeparator(String c) {
        return SPACE_SEPARATOR.matcher(c).matches();
    }

    boolean isIdStartChar(String c) {
        char ch = c.charAt(0);
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '$' || ch == '_' || ID_START.matcher(c).matches();
    }

    boolean isIdContinuationChar(String s) {
        char c = s.charAt(0);
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '$' || c == '_' || c == '\u200C' || c == '\u200D' || ID_CONTINUE.matcher(s).matches();
    }

    boolean isDigit(String s) {
        return s.matches("[0-9]");
    }

    boolean isHexDigit(String s) {
        return s.matches("[0-9A-Fa-f]");
    }

    ParseState parseStates = new ParseState();

    /**
     * @return a List if the input is an array; a Map if the input is an object
     * @throws SyntaxError on invalid content
     */
    public Object parse() throws SyntaxError {
        parseState = START;
        stack = new ArrayList<>();
        pos = 0;
        line = 1;
        column = 0;
        do {
            token = lex();
            if (token != null)
                parseStates.state();
        }
        while (token != null && !token.type.equals(EOF));
        return root;
    }

    LexState lexStates = new LexState();
    String lexState;
    String buffer;
    boolean doubleQuote;
    int sign;
    char c;

    Token lex() throws SyntaxError {
        lexState = DEFAULT;
        buffer = "";
        doubleQuote = false;
        sign = 1;
        while (pos < source.length()) {
            c = peek();
            final Token token = lexStates.state(lexState);
            if (token != null)
                return token;
        }
        return null;
    }

    char peek() {
        return source.charAt(pos);
    }

    char read() {
        char c = peek();
        if (c == '\n') {
            line++;
            column = 0;
        } else
            column++;
        pos++;
        return c;
    }

    void literal(String s) throws SyntaxError {
        for (char c : s.toCharArray()) {
            char p = peek();
            if (p != c)
                throw invalidChar(read());
            read();
        }
    }

    static class Token {
        String type;
        String value;

        public Token(String type_) {
            type = type_;
        }


        public Token(String s, Object b) {
            type = s;
            value = String.valueOf(b);
        }
    }

    class ParseState {
        void start() throws SyntaxError {
            if (token.type.equals(EOF))
                throw invalidEOF();
            push();
        }

        @SuppressWarnings("unchecked")
        private void push() {
            Object value = null;
            switch (token.type) {
                case PUNCTUATOR:
                    switch (token.value) {
                        case "{":
                            value = new LinkedHashMap<>();
                            break;
                        case "[":
                            value = new ArrayList<>();
                            break;
                    }
                    break;
                case "null":
                case BOOLEAN:
                case NUMERIC:
                case STRING:
                    value = token.value;
                    break;
            }
            if (root == null) {
                root = value;
            } else {
                Object parent = stack.get(stack.size() - 1);
                if (parent instanceof List) {
                    ((List<Object>) parent).add(value);
                } else if (parent instanceof Map) {
                    ((Map<Object, Object>) parent).put(key, value);
                }
            }


            if (value instanceof List || value instanceof Map) {
                stack.add(value);
                if (value instanceof List)
                    parseState = BEFORE_ARRAY_VALUE;
                else
                    parseState = BEFORE_PROPERTY_NAME;
            } else {
                Object current = stack.get(stack.size() - 1);
                if (current instanceof List) {
                    parseState = AFTER_ARRAY_VALUE;
                } else if (current instanceof Map) {
                    parseState = AFTER_PROPERTY_VALUE;
                } else parseState = END;
            }

        }

        void pop() {
            stack.remove(stack.size() - 1);
            if (stack.size() > 0) {
                Object current = stack.get(stack.size() - 1);
                if (current == null) {
                    parseState = END;
                } else if (current instanceof List) {
                    parseState = AFTER_ARRAY_VALUE;
                } else {
                    parseState = AFTER_PROPERTY_VALUE;
                }
            }
        }

        public void state() throws SyntaxError {
            switch (parseState) {
                case START:
                    start();
                    break;
                case BEFORE_PROPERTY_NAME:
                    switch (token.type) {
                        case IDENTIFIER:
                        case STRING:
                            key = token.value;
                            parseState = AFTER_PROPERTY_NAME;
                            return;
                        case PUNCTUATOR:
                            pop();
                            return;
                        case EOF:
                            throw invalidEOF();
                    }
                case AFTER_PROPERTY_NAME:
                    if (token.type.equals(EOF)) {
                        throw invalidEOF();
                    }
                    parseState = BEFORE_PROPERTY_VALUE;
                    break;
                case BEFORE_PROPERTY_VALUE:
                    if (token.type.equals(EOF)) {
                        throw invalidEOF();
                    }

                    push();
                    break;
                case BEFORE_ARRAY_VALUE:
                    if (token.type.equals(EOF)) {
                        throw invalidEOF();
                    }
                    if (token.type.equals(PUNCTUATOR) && token.value.equals("]")) {
                        pop();
                        return;
                    }
                    push();
                case AFTER_PROPERTY_VALUE:
                    if (token.type.equals(EOF)) {
                        throw invalidEOF();
                    }
                    switch (token.value) {
                        case ",":
                            parseState = BEFORE_PROPERTY_NAME;
                            return;
                        case "}":
                            pop();
                    }
                    break;
                case AFTER_ARRAY_VALUE:
                    if (token.type.equals(EOF)) {
                        throw invalidEOF();
                    }

                    switch (token.value) {
                        case ",":
                            parseState = BEFORE_ARRAY_VALUE;
                            return;
                        case "]":
                            pop();
                    }
                case END: {

                }
            }
        }
    }

    class LexState {

        Token state(String parseState) throws SyntaxError {
            switch (parseState) {
                case DEFAULT:
                    return default_();
                case COMMENT:
                    return comment();
                case MULTI_LINE_COMMENT:
                    return multilineComment();
                case MULTI_LINE_COMMENT_ASTERISK:
                    return multiLineCommentAsterisk();
                case SINGLE_LINE_COMMENT:
                    return singleLineComment();
                case VALUE:
                    return value();
                case IDENTIFIER_NAME_START_ESCAPE:
                    identifierNameStartEscape();
                    break;
                case IDENTIFIER_NAME:
                    return identifierName();
                case IDENTIFIER_NAME_ESCAPE:
                    identifierNameEscape();
                    break;
                case SIGN:
                    return sign();
                case ZERO:
                    return zero();
                case DECIMAL_INTEGER:
                    return decimalInteger();
                case DECIMAL_POINT_LEADING:
                    decimalPointLeading();
                    break;
                case DECIMAL_POINT:
                    return decimalPoint();
                case DECIMAL_FRACTION:
                    return decimalFraction();
                case DECIMAL_EXPONENT:
                    decimalExponent();
                    break;
                case DECIMAL_EXPONENT_SIGN:
                    decimalExponentSing();
                    break;
                case DECIMAL_EXPONENT_INTEGER:
                    return decimalExponentInteger();
                case HEXADECIMAL:
                    hexadecimal();
                    break;
                case HEXADECIMAL_INTEGER:
                    return hexadecimalInteger();
                case STRING:
                    return string();
                case START:
                    return start();
                case BEFORE_PROPERTY_NAME:
                    return beforePropertyName();
                case AFTER_PROPERTY_NAME:
                    return afterPropertyName();
                case BEFORE_PROPERTY_VALUE:
                    beforePropertyValue();
                    break;
                case AFTER_PROPERTY_VALUE:
                    return afterPropertyValue();
                case BEFORE_ARRAY_VALUE:
                    return beforeArrayValue();
                case AFTER_ARRAY_VALUE:
                    return afterArrayValue();
                case END:
                    end();
                    break;
                default:
                    return null;
            }
            return null;
        }

        Token default_() throws SyntaxError {
            switch (c) {
                case '\t':
                case '\f':
                case ' ':
                case '\u00A0':
                case '\uFEFF':
                case '\n':
                case '\r':
                case '\u2028':
                case '\u2029':
                    read();
                    return null;
                case '/':
                    read();
                    lexState = COMMENT;
                    return null;
                case 0:
                    read();
                    return new Token(EOF);
            }

            if (isSpaceSeparator(String.valueOf(c))) {
                read();
                return null;
            }
            return state(parseState);

        }

        Token comment() throws SyntaxError {
            switch (c) {
                case '*':
                    read();
                    lexState = MULTI_LINE_COMMENT;
                    return null;
                case '/':
                    read();
                    lexState = SINGLE_LINE_COMMENT;
                    return null;
            }
            throw invalidChar(read());
        }

        Token multilineComment() throws SyntaxError {
            switch (c) {
                case '*':
                    read();
                    lexState = MULTI_LINE_COMMENT_ASTERISK;

                    return null;
                case 0:
                    throw invalidChar(read());
            }
            read();
            return null;
        }

        Token multiLineCommentAsterisk() throws SyntaxError {
            switch (c) {
                case '*':
                    read();
                    return null;
                case '/':
                    read();
                    lexState = DEFAULT;
                    return null;
                case 0:
                    throw invalidChar(read());
            }
            read();
            lexState = MULTI_LINE_COMMENT;
            return null;
        }

        Token singleLineComment() {
            switch (c) {
                case '\n':
                case '\r':
                case '\u2029':
                case '\u2028':
                    read();
                    lexState = DEFAULT;
                    return null;
                case 0:
                    read();
                    return new Token(EOF);
            }
            read();
            return null;
        }

        Token value() throws SyntaxError {
            switch (c) {
                case '{':
                case '[':
                    return new Token(PUNCTUATOR, read());
                case 'n':
                    read();
                    literal("ull");
                    return new Token("null", null);
                case 't':
                    read();
                    literal("rue");
                    return new Token(BOOLEAN, true);
                case 'f':
                    read();
                    literal("alse");
                    return new Token(BOOLEAN, false);
                case '-':
                case '+':
                    if (read() == '-')
                        sign = -1;
                    lexState = SIGN;
                    return null;
                case '.':
                    buffer = "" + read();
                    lexState = DECIMAL_POINT_LEADING;
                    return null;
                case '0':
                    buffer = "" + read();
                    lexState = ZERO;
                    return null;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    buffer = "" + read();
                    lexState = DECIMAL_INTEGER;
                    return null;
                case 'I':
                    read();
                    literal("nfinity");
                    return new Token(NUMERIC, Double.POSITIVE_INFINITY);
                case 'N':
                    read();
                    literal("aN");
                    return new Token(NUMERIC, Double.NaN);
                case '"':
                case '\'':
                    doubleQuote = (read() == '"');
                    buffer = "";
                    lexState = STRING;
                    return null;
            }
            throw invalidChar(read());
        }

        void identifierNameStartEscape() throws SyntaxError {
            if (c != 'u')
                throw invalidChar(read());
            read();
            char u = unicodeEscape();
            switch (u) {
                case '$':
                case '_':
                    break;
                default:
                    if (!isIdStartChar("" + u))
                        throw invalidIdentifier();

            }
            buffer += u;
            lexState = IDENTIFIER_NAME;
        }

        Token identifierName() {
            switch (c) {
                case '$':
                case '_':
                case '\u200C':
                case '\u200D':
                    buffer += read();
                    return null;

                case '\\':
                    read();
                    lexState = IDENTIFIER_NAME_ESCAPE;
                    return null;
            }
            if (isIdContinuationChar("" + c)) {
                buffer += read();
                return null;
            }

            return new Token(IDENTIFIER, buffer);
        }

        void identifierNameEscape() throws SyntaxError {
            if (c != 'u')
                throw invalidChar(read());
            read();
            char u = unicodeEscape();
            switch (u) {
                case '$':
                case '_':
                case '\u200C':
                case '\u200D':
                    break;
                default:
                    if (!isIdContinuationChar("" + u))
                        throw invalidIdentifier();
            }
            buffer += u;
            lexState = IDENTIFIER_NAME;
        }

        Token sign() throws SyntaxError {
            switch (c) {
                case '.':
                    buffer = "" + read();
                    lexState = DECIMAL_POINT_LEADING;
                    return null;
                case '0':
                    buffer = "" + read();
                    lexState = ZERO;
                    return null;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    buffer = "" + read();
                    lexState = DECIMAL_INTEGER;
                    return null;
                case 'I':
                    read();
                    literal("nfinity");
                    return new Token(NUMERIC, sign * Double.POSITIVE_INFINITY);
                case 'N':
                    read();
                    literal("aN");
                    return new Token(NUMERIC, Double.NaN);

            }
            throw invalidChar(read());
        }

        Token zero() {
            switch (c) {
                case '.':
                    buffer += read();
                    lexState = DECIMAL_POINT;
                    return null;
                case 'e':
                case 'E':
                    buffer += read();
                    lexState = DECIMAL_EXPONENT;
                    return null;
                case 'x':
                case 'X':
                    buffer += read();
                    lexState = HEXADECIMAL;
                    return null;
            }
            return new Token(NUMERIC, 0);
        }

        Token decimalInteger() {
            switch (c) {
                case '.':
                    buffer += read();
                    lexState = DECIMAL_POINT;
                    return null;
                case 'e':
                case 'E':
                    buffer += read();
                    lexState = DECIMAL_EXPONENT;
                    return null;
            }
            if (isDigit(c + "")) {
                buffer += read();
                return null;
            }
            return new Token(NUMERIC, sign * Integer.parseInt(buffer));
        }

        void decimalPointLeading() throws SyntaxError {
            if (isDigit(c + "")) {
                buffer += read();
                lexState = DECIMAL_FRACTION;
                return;
            }
            throw invalidChar(read());
        }

        Token decimalPoint() {
            switch (c) {
                case 'e':
                case 'E':
                    buffer += read();
                    lexState = DECIMAL_EXPONENT;
                    return null;
            }
            if (isDigit(c + "")) {
                buffer += read();
                lexState = DECIMAL_FRACTION;
                return null;
            }
            return new Token(NUMERIC, sign * Double.parseDouble(buffer));
        }

        Token decimalFraction() {
            switch (c) {
                case 'e':
                case 'E':
                    buffer += read();
                    lexState = DECIMAL_EXPONENT;
                    return null;
            }
            if (isDigit(c + "")) {
                buffer += read();
                return null;
            }
            return new Token(NUMERIC, sign * Double.parseDouble(buffer));
        }

        void decimalExponent() throws SyntaxError {
            switch (c) {
                case '+':
                case '-':
                    buffer += read();
                    lexState = DECIMAL_EXPONENT_SIGN;
                    return;
            }
            if (isHexDigit(c + "")) {
                buffer += read();
                lexState = DECIMAL_EXPONENT_INTEGER;
                return;
            }
            throw invalidChar(read());
        }

        void decimalExponentSing() throws SyntaxError {
            if (isDigit(c + "")) {
                buffer += read();
                lexState = DECIMAL_EXPONENT_INTEGER;
                return;
            }
            throw invalidChar(read());
        }

        Token decimalExponentInteger() {
            if (isDigit(c + "")) {
                buffer += read();
                return null;
            }
            return new Token(NUMERIC, sign * Double.parseDouble(buffer));
        }

        void hexadecimal() throws SyntaxError {
            if (isHexDigit(c + "")) {
                buffer += read();
                lexState = HEXADECIMAL_INTEGER;
                return;
            }
            throw invalidChar(read());
        }

        Token hexadecimalInteger() {
            if (isHexDigit(c + "")) {
                buffer += read();
                return null;
            }
            return new Token(NUMERIC, sign * Integer.parseInt(buffer.substring(2), 16));
        }

        Token string() throws SyntaxError {
            switch (c) {
                case '\\':
                    read();
                    buffer += escape();
                    return null;
                case '"':
                    if (doubleQuote) {
                        read();
                        return new Token(STRING, buffer);
                    }
                    buffer += read();
                    return null;
                case '\'':
                    if (!doubleQuote) {
                        read();
                        return new Token(STRING, buffer);
                    }
                    buffer += read();
                    return null;
                case '\n':
                case '\r':
                    throw invalidChar(read());
                case '\u2028':
                case '\u2029':
                    separatorChar(c);
                case 0:
                    throw invalidChar(read());
            }
            buffer += read();
            return null;
        }

        Token start() {
            switch (c) {
                case '{':
                case '[':
                    return new Token(PUNCTUATOR, read());
            }
            lexState = VALUE;
            return null;
        }

        Token beforePropertyName() throws SyntaxError {
            switch (c) {
                case '$':
                case '_':
                    buffer = "" + read();
                    lexState = IDENTIFIER_NAME;
                    return null;
                case '\\':
                    read();
                    lexState = IDENTIFIER_NAME_START_ESCAPE;
                    return null;
                case '}':
                    return new Token(PUNCTUATOR, read());
                case '"':
                case '\'':
                    doubleQuote = (read() == '"');
                    lexState = STRING;
                    return null;
            }
            if (isIdStartChar(c + "")) {
                buffer += read();
                lexState = IDENTIFIER_NAME;
                return null;
            }
            throw invalidChar(read());
        }

        Token afterPropertyName() throws SyntaxError {
            if (c == ':')
                return new Token(PUNCTUATOR, read());
            throw invalidChar(read());
        }

        void beforePropertyValue() {
            lexState = VALUE;
        }

        Token afterPropertyValue() throws SyntaxError {
            switch (c) {
                case ',':
                case '}':
                    return new Token(PUNCTUATOR, read());
            }
            throw invalidChar(read());
        }

        Token beforeArrayValue() {
            if (c == ']')
                return new Token(PUNCTUATOR, read());
            lexState = VALUE;
            return null;
        }

        Token afterArrayValue() throws SyntaxError {
            switch (c) {
                case ',':
                case ']':
                    return new Token(PUNCTUATOR, read());
            }
            throw invalidChar(read());
        }

        void end() throws SyntaxError {
            throw invalidChar(read());
        }

        private void separatorChar(char c) {
            System.out.println(formatChar(c) + " in strings is not valid ECMAScript, consider escaping");
        }
    }

    char escape() throws SyntaxError {
        char c = peek();
        switch (c) {
            case 'b':
                read();
                return '\b';
            case 'f':
                read();
                return '\f';
            case 'n':
                read();
                return '\n';
            case 'r':
                read();
                return '\r';
            case 't':
                read();
                return '\t';
            case '0':
                read();
                if (isDigit(peek() + ""))
                    throw invalidChar(read());
                return '\0';
            case 'x':
                read();
                return hexEscape();
            case 'u':
                read();
                return unicodeEscape();
            case '\n':
            case '\u2028':
            case '\u2029':
                read();
                return 0;
            case '\r':
                read();
                if (peek() == '\n')
                    read();
                return 0;

            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':

            case 0:
                throw invalidChar(read());
        }
        return read();
    }

    private char unicodeEscape() throws SyntaxError {
        StringBuilder buffer = new StringBuilder();
        int count = 4;
        while (count-- > 0) {
            char c = peek();
            if (!isHexDigit(c + "")) {
                throw invalidChar(read());
            }
            buffer.append(read());
        }
        return (char) Integer.parseInt(buffer.toString(), 16);
    }

    private char hexEscape() throws SyntaxError {
        String buffer = "";
        char c = peek();
        if (!isHexDigit(c + "")) {
            throw invalidChar(read());
        }
        buffer += read();
        c = peek();
        if (!isHexDigit(c + ""))
            throw invalidChar(read());
        buffer += read();
        return (char) Integer.parseInt(buffer, 16);
    }


    SyntaxError invalidChar(char c) {
        if (c == 0)
            return syntaxError("Invalid end of input at " + line + ":" + column);
        return syntaxError("invalid character " + formatChar(c) + " at " + line + ":" + column);
    }

    SyntaxError invalidEOF() {
        return syntaxError("invalid end of input at " + line + ":" + column);
    }

    SyntaxError invalidIdentifier() {
        column -= 5;
        return syntaxError("Invalid identifier character at " + line + ":" + column);
    }

    static HashMap<Character, String> replacements = new HashMap<>();

    static {
        replacements.put('\'', "\\'");
        replacements.put('"', "\"");
        replacements.put('\\', "\\\\");
        replacements.put('\b', "\\b");
        replacements.put('\f', "\\f");
        replacements.put('\n', "\\n");
        replacements.put('\r', "\\r");
        replacements.put('\t', "\\t");
        replacements.put('\0', "\\0");
        replacements.put('\u2028', "\\u2028");
        replacements.put('\u2029', "\\u2029");
    }

    private String formatChar(char c) {
        if (replacements.get(c) != null)
            return replacements.get(c);
        if (c < ' ') {
            String hexString = String.valueOf(Integer.parseInt(c + "", 16));
            return "\\x" + ("00" + hexString).substring(hexString.length());
        }
        return c + "";
    }

    SyntaxError syntaxError(String message) {
        return new SyntaxError(message);
    }


}
