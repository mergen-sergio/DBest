package dsl.antlr4;

// Generated from RelAlgebra.g4 by ANTLR 4.7.2
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class RelAlgebraLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, SELECTION=7, PROJECTION=8, 
		JOIN=9, LEFTJOIN=10, RIGHTJOIN=11, UNION=12, CARTESIANPRODUCT=13, ATTRIBUTE=14, 
		PREDICATE=15, RELATION=16, DIGIT=17, WS=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "SELECTION", "PROJECTION", 
			"JOIN", "LEFTJOIN", "RIGHTJOIN", "UNION", "CARTESIANPRODUCT", "ATTRIBUTE", 
			"PREDICATE", "RELATION", "DIGIT", "WS", "A", "B", "C", "D", "E", "F", 
			"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", 
			"U", "V", "W", "X", "Y", "Z"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "'<'", "','", "'>'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, "SELECTION", "PROJECTION", 
			"JOIN", "LEFTJOIN", "RIGHTJOIN", "UNION", "CARTESIANPRODUCT", "ATTRIBUTE", 
			"PREDICATE", "RELATION", "DIGIT", "WS"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public RelAlgebraLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "RelAlgebra.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24\u0101\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16"+
		"\3\16\3\16\3\17\3\17\7\17\u00ae\n\17\f\17\16\17\u00b1\13\17\3\17\3\17"+
		"\3\20\3\20\7\20\u00b7\n\20\f\20\16\20\u00ba\13\20\3\20\3\20\3\21\3\21"+
		"\7\21\u00c0\n\21\f\21\16\21\u00c3\13\21\3\22\3\22\3\23\6\23\u00c8\n\23"+
		"\r\23\16\23\u00c9\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3"+
		"\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3"+
		"\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\3\'\3\'\3(\3(\3)"+
		"\3)\3*\3*\3+\3+\3,\3,\3-\3-\4\u00af\u00b8\2.\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\2)\2"+
		"+\2-\2/\2\61\2\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2S"+
		"\2U\2W\2Y\2\3\2 \4\2C\\c|\6\2\62;C\\aac|\3\2\62;\5\2\13\f\17\17\"\"\4"+
		"\2CCcc\4\2DDdd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKk"+
		"k\4\2LLll\4\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2"+
		"TTtt\4\2UUuu\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\|"+
		"|\2\u00ea\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2"+
		"\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27"+
		"\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2"+
		"\2\2#\3\2\2\2\2%\3\2\2\2\3[\3\2\2\2\5]\3\2\2\2\7_\3\2\2\2\ta\3\2\2\2\13"+
		"c\3\2\2\2\re\3\2\2\2\17g\3\2\2\2\21q\3\2\2\2\23|\3\2\2\2\25\u0081\3\2"+
		"\2\2\27\u008a\3\2\2\2\31\u0094\3\2\2\2\33\u009a\3\2\2\2\35\u00ab\3\2\2"+
		"\2\37\u00b4\3\2\2\2!\u00bd\3\2\2\2#\u00c4\3\2\2\2%\u00c7\3\2\2\2\'\u00cd"+
		"\3\2\2\2)\u00cf\3\2\2\2+\u00d1\3\2\2\2-\u00d3\3\2\2\2/\u00d5\3\2\2\2\61"+
		"\u00d7\3\2\2\2\63\u00d9\3\2\2\2\65\u00db\3\2\2\2\67\u00dd\3\2\2\29\u00df"+
		"\3\2\2\2;\u00e1\3\2\2\2=\u00e3\3\2\2\2?\u00e5\3\2\2\2A\u00e7\3\2\2\2C"+
		"\u00e9\3\2\2\2E\u00eb\3\2\2\2G\u00ed\3\2\2\2I\u00ef\3\2\2\2K\u00f1\3\2"+
		"\2\2M\u00f3\3\2\2\2O\u00f5\3\2\2\2Q\u00f7\3\2\2\2S\u00f9\3\2\2\2U\u00fb"+
		"\3\2\2\2W\u00fd\3\2\2\2Y\u00ff\3\2\2\2[\\\7=\2\2\\\4\3\2\2\2]^\7>\2\2"+
		"^\6\3\2\2\2_`\7.\2\2`\b\3\2\2\2ab\7@\2\2b\n\3\2\2\2cd\7*\2\2d\f\3\2\2"+
		"\2ef\7+\2\2f\16\3\2\2\2gh\5K&\2hi\5/\30\2ij\5=\37\2jk\5/\30\2kl\5+\26"+
		"\2lm\5M\'\2mn\5\67\34\2no\5C\"\2op\5A!\2p\20\3\2\2\2qr\5E#\2rs\5I%\2s"+
		"t\5C\"\2tu\59\35\2uv\5/\30\2vw\5+\26\2wx\5M\'\2xy\5\67\34\2yz\5C\"\2z"+
		"{\5A!\2{\22\3\2\2\2|}\59\35\2}~\5C\"\2~\177\5\67\34\2\177\u0080\5A!\2"+
		"\u0080\24\3\2\2\2\u0081\u0082\5=\37\2\u0082\u0083\5/\30\2\u0083\u0084"+
		"\5\61\31\2\u0084\u0085\5M\'\2\u0085\u0086\59\35\2\u0086\u0087\5C\"\2\u0087"+
		"\u0088\5\67\34\2\u0088\u0089\5A!\2\u0089\26\3\2\2\2\u008a\u008b\5I%\2"+
		"\u008b\u008c\5\67\34\2\u008c\u008d\5\63\32\2\u008d\u008e\5\65\33\2\u008e"+
		"\u008f\5M\'\2\u008f\u0090\59\35\2\u0090\u0091\5C\"\2\u0091\u0092\5\67"+
		"\34\2\u0092\u0093\5A!\2\u0093\30\3\2\2\2\u0094\u0095\5O(\2\u0095\u0096"+
		"\5A!\2\u0096\u0097\5\67\34\2\u0097\u0098\5C\"\2\u0098\u0099\5A!\2\u0099"+
		"\32\3\2\2\2\u009a\u009b\5+\26\2\u009b\u009c\5\'\24\2\u009c\u009d\5I%\2"+
		"\u009d\u009e\5M\'\2\u009e\u009f\5/\30\2\u009f\u00a0\5K&\2\u00a0\u00a1"+
		"\5\67\34\2\u00a1\u00a2\5\'\24\2\u00a2\u00a3\5A!\2\u00a3\u00a4\5E#\2\u00a4"+
		"\u00a5\5I%\2\u00a5\u00a6\5C\"\2\u00a6\u00a7\5-\27\2\u00a7\u00a8\5O(\2"+
		"\u00a8\u00a9\5+\26\2\u00a9\u00aa\5M\'\2\u00aa\34\3\2\2\2\u00ab\u00af\7"+
		")\2\2\u00ac\u00ae\13\2\2\2\u00ad\u00ac\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af"+
		"\u00b0\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0\u00b2\3\2\2\2\u00b1\u00af\3\2"+
		"\2\2\u00b2\u00b3\7)\2\2\u00b3\36\3\2\2\2\u00b4\u00b8\7]\2\2\u00b5\u00b7"+
		"\13\2\2\2\u00b6\u00b5\3\2\2\2\u00b7\u00ba\3\2\2\2\u00b8\u00b9\3\2\2\2"+
		"\u00b8\u00b6\3\2\2\2\u00b9\u00bb\3\2\2\2\u00ba\u00b8\3\2\2\2\u00bb\u00bc"+
		"\7_\2\2\u00bc \3\2\2\2\u00bd\u00c1\t\2\2\2\u00be\u00c0\t\3\2\2\u00bf\u00be"+
		"\3\2\2\2\u00c0\u00c3\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2"+
		"\"\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c4\u00c5\t\4\2\2\u00c5$\3\2\2\2\u00c6"+
		"\u00c8\t\5\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00c7\3\2"+
		"\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00cc\b\23\2\2\u00cc"+
		"&\3\2\2\2\u00cd\u00ce\t\6\2\2\u00ce(\3\2\2\2\u00cf\u00d0\t\7\2\2\u00d0"+
		"*\3\2\2\2\u00d1\u00d2\t\b\2\2\u00d2,\3\2\2\2\u00d3\u00d4\t\t\2\2\u00d4"+
		".\3\2\2\2\u00d5\u00d6\t\n\2\2\u00d6\60\3\2\2\2\u00d7\u00d8\t\13\2\2\u00d8"+
		"\62\3\2\2\2\u00d9\u00da\t\f\2\2\u00da\64\3\2\2\2\u00db\u00dc\t\r\2\2\u00dc"+
		"\66\3\2\2\2\u00dd\u00de\t\16\2\2\u00de8\3\2\2\2\u00df\u00e0\t\17\2\2\u00e0"+
		":\3\2\2\2\u00e1\u00e2\t\20\2\2\u00e2<\3\2\2\2\u00e3\u00e4\t\21\2\2\u00e4"+
		">\3\2\2\2\u00e5\u00e6\t\22\2\2\u00e6@\3\2\2\2\u00e7\u00e8\t\23\2\2\u00e8"+
		"B\3\2\2\2\u00e9\u00ea\t\24\2\2\u00eaD\3\2\2\2\u00eb\u00ec\t\25\2\2\u00ec"+
		"F\3\2\2\2\u00ed\u00ee\t\26\2\2\u00eeH\3\2\2\2\u00ef\u00f0\t\27\2\2\u00f0"+
		"J\3\2\2\2\u00f1\u00f2\t\30\2\2\u00f2L\3\2\2\2\u00f3\u00f4\t\31\2\2\u00f4"+
		"N\3\2\2\2\u00f5\u00f6\t\32\2\2\u00f6P\3\2\2\2\u00f7\u00f8\t\33\2\2\u00f8"+
		"R\3\2\2\2\u00f9\u00fa\t\34\2\2\u00faT\3\2\2\2\u00fb\u00fc\t\35\2\2\u00fc"+
		"V\3\2\2\2\u00fd\u00fe\t\36\2\2\u00feX\3\2\2\2\u00ff\u0100\t\37\2\2\u0100"+
		"Z\3\2\2\2\7\2\u00af\u00b8\u00c1\u00c9\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}