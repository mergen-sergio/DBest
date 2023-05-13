package dsl.antlr4;

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
		T__0=1, T__1=2, T__2=3, SELECTION=4, PROJECTION=5, JOIN=6, CARTESIAN=7, 
		ATTRIBUTE=8, PREDICATE=9, RELATION=10, WS=11;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "SELECTION", "PROJECTION", "JOIN", "CARTESIAN", 
			"ATTRIBUTE", "PREDICATE", "RELATION", "WS", "A", "B", "C", "D", "E", 
			"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", 
			"T", "U", "V", "W", "X", "Y", "Z"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "','"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, "SELECTION", "PROJECTION", "JOIN", "CARTESIAN", 
			"ATTRIBUTE", "PREDICATE", "RELATION", "WS"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\r\u00cb\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3"+
		"\t\7\tz\n\t\f\t\16\t}\13\t\3\t\3\t\3\n\3\n\7\n\u0083\n\n\f\n\16\n\u0086"+
		"\13\n\3\n\3\n\3\13\3\13\7\13\u008c\n\13\f\13\16\13\u008f\13\13\3\f\6\f"+
		"\u0092\n\f\r\f\16\f\u0093\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3\17\3\20\3\20"+
		"\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27"+
		"\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36"+
		"\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3&\3&\4{\u0084\2\'\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\2\33\2\35\2\37\2"+
		"!\2#\2%\2\'\2)\2+\2-\2/\2\61\2\63\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G\2"+
		"I\2K\2\3\2\37\4\2C\\c|\6\2\62;C\\aac|\5\2\13\f\17\17\"\"\4\2CCcc\4\2D"+
		"Ddd\4\2EEee\4\2FFff\4\2GGgg\4\2HHhh\4\2IIii\4\2JJjj\4\2KKkk\4\2LLll\4"+
		"\2MMmm\4\2NNnn\4\2OOoo\4\2PPpp\4\2QQqq\4\2RRrr\4\2SSss\4\2TTtt\4\2UUu"+
		"u\4\2VVvv\4\2WWww\4\2XXxx\4\2YYyy\4\2ZZzz\4\2[[{{\4\2\\\\||\2\u00b4\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\3"+
		"M\3\2\2\2\5O\3\2\2\2\7Q\3\2\2\2\tS\3\2\2\2\13]\3\2\2\2\rh\3\2\2\2\17m"+
		"\3\2\2\2\21w\3\2\2\2\23\u0080\3\2\2\2\25\u0089\3\2\2\2\27\u0091\3\2\2"+
		"\2\31\u0097\3\2\2\2\33\u0099\3\2\2\2\35\u009b\3\2\2\2\37\u009d\3\2\2\2"+
		"!\u009f\3\2\2\2#\u00a1\3\2\2\2%\u00a3\3\2\2\2\'\u00a5\3\2\2\2)\u00a7\3"+
		"\2\2\2+\u00a9\3\2\2\2-\u00ab\3\2\2\2/\u00ad\3\2\2\2\61\u00af\3\2\2\2\63"+
		"\u00b1\3\2\2\2\65\u00b3\3\2\2\2\67\u00b5\3\2\2\29\u00b7\3\2\2\2;\u00b9"+
		"\3\2\2\2=\u00bb\3\2\2\2?\u00bd\3\2\2\2A\u00bf\3\2\2\2C\u00c1\3\2\2\2E"+
		"\u00c3\3\2\2\2G\u00c5\3\2\2\2I\u00c7\3\2\2\2K\u00c9\3\2\2\2MN\7*\2\2N"+
		"\4\3\2\2\2OP\7+\2\2P\6\3\2\2\2QR\7.\2\2R\b\3\2\2\2ST\5=\37\2TU\5!\21\2"+
		"UV\5/\30\2VW\5!\21\2WX\5\35\17\2XY\5? \2YZ\5)\25\2Z[\5\65\33\2[\\\5\63"+
		"\32\2\\\n\3\2\2\2]^\5\67\34\2^_\5;\36\2_`\5\65\33\2`a\5+\26\2ab\5!\21"+
		"\2bc\5\35\17\2cd\5? \2de\5)\25\2ef\5\65\33\2fg\5\63\32\2g\f\3\2\2\2hi"+
		"\5+\26\2ij\5\65\33\2jk\5)\25\2kl\5\63\32\2l\16\3\2\2\2mn\5\35\17\2no\5"+
		"\31\r\2op\5;\36\2pq\5? \2qr\5!\21\2rs\5=\37\2st\5)\25\2tu\5\31\r\2uv\5"+
		"\63\32\2v\20\3\2\2\2w{\7)\2\2xz\13\2\2\2yx\3\2\2\2z}\3\2\2\2{|\3\2\2\2"+
		"{y\3\2\2\2|~\3\2\2\2}{\3\2\2\2~\177\7)\2\2\177\22\3\2\2\2\u0080\u0084"+
		"\7]\2\2\u0081\u0083\13\2\2\2\u0082\u0081\3\2\2\2\u0083\u0086\3\2\2\2\u0084"+
		"\u0085\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0087\3\2\2\2\u0086\u0084\3\2"+
		"\2\2\u0087\u0088\7_\2\2\u0088\24\3\2\2\2\u0089\u008d\t\2\2\2\u008a\u008c"+
		"\t\3\2\2\u008b\u008a\3\2\2\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2\2\2\u008d"+
		"\u008e\3\2\2\2\u008e\26\3\2\2\2\u008f\u008d\3\2\2\2\u0090\u0092\t\4\2"+
		"\2\u0091\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0091\3\2\2\2\u0093\u0094"+
		"\3\2\2\2\u0094\u0095\3\2\2\2\u0095\u0096\b\f\2\2\u0096\30\3\2\2\2\u0097"+
		"\u0098\t\5\2\2\u0098\32\3\2\2\2\u0099\u009a\t\6\2\2\u009a\34\3\2\2\2\u009b"+
		"\u009c\t\7\2\2\u009c\36\3\2\2\2\u009d\u009e\t\b\2\2\u009e \3\2\2\2\u009f"+
		"\u00a0\t\t\2\2\u00a0\"\3\2\2\2\u00a1\u00a2\t\n\2\2\u00a2$\3\2\2\2\u00a3"+
		"\u00a4\t\13\2\2\u00a4&\3\2\2\2\u00a5\u00a6\t\f\2\2\u00a6(\3\2\2\2\u00a7"+
		"\u00a8\t\r\2\2\u00a8*\3\2\2\2\u00a9\u00aa\t\16\2\2\u00aa,\3\2\2\2\u00ab"+
		"\u00ac\t\17\2\2\u00ac.\3\2\2\2\u00ad\u00ae\t\20\2\2\u00ae\60\3\2\2\2\u00af"+
		"\u00b0\t\21\2\2\u00b0\62\3\2\2\2\u00b1\u00b2\t\22\2\2\u00b2\64\3\2\2\2"+
		"\u00b3\u00b4\t\23\2\2\u00b4\66\3\2\2\2\u00b5\u00b6\t\24\2\2\u00b68\3\2"+
		"\2\2\u00b7\u00b8\t\25\2\2\u00b8:\3\2\2\2\u00b9\u00ba\t\26\2\2\u00ba<\3"+
		"\2\2\2\u00bb\u00bc\t\27\2\2\u00bc>\3\2\2\2\u00bd\u00be\t\30\2\2\u00be"+
		"@\3\2\2\2\u00bf\u00c0\t\31\2\2\u00c0B\3\2\2\2\u00c1\u00c2\t\32\2\2\u00c2"+
		"D\3\2\2\2\u00c3\u00c4\t\33\2\2\u00c4F\3\2\2\2\u00c5\u00c6\t\34\2\2\u00c6"+
		"H\3\2\2\2\u00c7\u00c8\t\35\2\2\u00c8J\3\2\2\2\u00c9\u00ca\t\36\2\2\u00ca"+
		"L\3\2\2\2\7\2{\u0084\u008d\u0093\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}