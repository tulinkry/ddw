<?php

namespace App\Presenters;

use Nette;


class HomepagePresenter extends Nette\Application\UI\Presenter
{
	protected $session;

	public function startup () {
		parent::startup ();
		$this -> session = $this -> getSession ('analyzator');
	}

	public function actionDefault () {
		$this -> template -> languages = [];
		$this -> template -> language = "";
		
		if ( isset($this -> session -> text) && ! empty( $this -> session -> text ) ) {
			$this -> template -> languages = $this -> analyze ( $this -> session -> text );
			$this -> template -> language = key( $this -> template -> languages );
			$this -> template -> score = reset ( $this -> template -> languages );
			$this["analyzeForm"]["analyze"] -> setDefaultValue ( $this -> session -> text );
		}

	}

	private function make_ngrams ( $text, $n ) {
		$array = [];
		$text = explode ( " ", $text );
		for ( $i = 0; $i < count($text) - $n + 1; $i ++ ) {
			$ngram = "";
			for ( $j = $i; $j < $i + $n; $j ++ ) {
				$ngram .= $text[$j];
				if ( $j != $i + $n - 1 )
					$ngram .= " ";
			}
			$array [] = $ngram;
		}
		return $array;
	}

	private function index_to_name ( $n ) {
		if ( $n == 1 )
			return "unigrams";
		if ( $n == 2 )
			return "bigrams";
		if ( $n == 3 )
			return "trigrams";
	}

	private function count_ngram_score ($text, $json, $n) {
		$score = 0;
		$array = $this -> make_ngrams ( $text, $n );
		for ( $i = 0; $i < count($array); $i ++ ) {
			if ( in_array( $array[$i], array_keys($json[$this->index_to_name($n)]) ) ) {
				$score += $json[$this->index_to_name($n)][$array[$i]];
			}
		}
		return $score;
	}

	public function analyze ( $text ) {

		$vectors = $this -> loadFile ();
		// print_r ( $vectors );
		$n = 3;
		$scores = [];
		for ( $i = 0; $i < $n; $i ++ ) {
			$local = [];
			foreach ( $vectors as $language => $vector ) {
				$local [ $language ] = $this -> count_ngram_score ( $text, $vector, $i + 1 );
			}
			$scores [] = $local;
		}

		$cmp = function ($a, $b) {
			return $a < $b;
		};

		$result = [];
		for ( $i = 0; $i < $n; $i ++ ) {
			uasort ( $scores[$i], $cmp );
			$result [] = $scores[$i];
		}

		foreach ( $vectors as $lang => $v ) {
			$counted_result [ $lang ] = 0;
			for ( $i = 0; $i < $n; $i ++ ) {
				$counted_result [ $lang ] += $result [$i] [ $lang ];
			}
		}


		uasort( $counted_result, $cmp );

		$sum = array_sum( $counted_result );
		
		if ( ! $sum )
			return [];

		foreach ( $counted_result as $lang => $val ) {
			$counted_result [ $lang ] = (int)(100 * $val / $sum);
		}

		return $counted_result;
	}

	private function loadFile () {
		$filename = $this -> context -> parameters [ "data_file" ];
		$content = file_get_contents($filename);
		return json_decode($content, true);
	}


	public function processForm ( $form ) {
		$text = $form -> values [ "analyze" ];

		if ( $text == "" || empty ( $text ) ) {
			$form -> addError ( "Text must be filled" );
			return;
		}


		$this -> session -> text = $text;
		$this -> redirect ( "this" );
	}


	protected function createComponentAnalyzeForm ( $name ) {
		$form = new Nette\Application\UI\Form;
		$wrappers = $form ->getRenderer() -> wrappers;
		$wrappers['controls']['container'] = NULL;
		$wrappers['pair']['container'] = 'div class="form-group clearfix"';
		$wrappers['pair']['.error'] = 'has-error';
		$wrappers['control']['container'] = 'div class=col-sm-12';
		$wrappers['label']['container'] = NULL;
		$wrappers['control']['description'] = NULL;
		$wrappers['control']['description'] = NULL;
		$wrappers['control']['errorcontainer'] = 'span class=help-block';
		$form -> getRenderer () -> wrappers = $wrappers;

		$form -> addTextArea ("analyze", NULL, "100%", 10)
			  -> getControlPrototype ()
			  	-> class = "form-control";
		$form -> addSubmit ( 'submit', 'Recognize' )
			  -> getControlPrototype ()
			  	-> class = "form-control btn btn-primary";
		$form -> onSuccess [] = array ( $this, "processForm" ); 

		return $this [ $name ] = $form;
	}
}
