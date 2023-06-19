/*
I have created SpellcheckWidget.js file and extends AbstractSpellcheckWidget.js .
but i want to know that what code i write in recuter.js file for spell corerction.I used solr 4.0 ALPHA

Manager.addWidget(new AjaxSolr.SpellcheckWidget({
       })); 

SpellcheckWidget.js files
*/

(function ($) {

// For an AutocompleteWidget that uses the q parameter, see:
// https://github.com/evolvingweb/ajax-solr/blob/gh-pages/examples/reuters/widgets/AutocompleteWidget.q.js
AjaxSolr.SpellcheckWidget = AjaxSolr.AbstractSpellcheckWidget.extend({
    
    
     suggestion: function () {
            var replacePairs = {};
            for (var word in this.suggestions) {
              replacePairs[word] = this.suggestions[word][0];
            }
            return this.manager.response.responseHeader.params['spellcheck.q'].strtr(replacePairs);
          },
          
          
          beforeRequest: function () {
                if (this.manager.store.get('spellcheck').val() && this.manager.store.get('q').val()) {
                  this.manager.store.get('spellcheck.q').val(this.manager.store.get('q').val());
                }
                else {
                  this.manager.store.remove('spellcheck.q');
                }
              },
 
              afterRequest: function () {
                    this.suggestions = {};
                    alert(this.manager.response.spellcheck);
                    if (this.manager.response.spellcheck && this.manager.response.spellcheck.suggestions) {
                      var suggestions = this.manager.response.spellcheck.suggestions;

                      for (var word in suggestions) {
                        if (word == 'collation' || word == 'correctlySpelled') continue;

                        this.suggestions[word] = [];
                        for (var i = 0, l = suggestions[word].suggestion.length; i < l; i++) {
                          if (this.manager.response.responseHeader.params['spellcheck.extendedResults']) {
                            this.suggestions[word].push(suggestions[word].suggestion[i].word);
                          }
                          else {
                            this.suggestions[word].push(suggestions[word].suggestion[i]);
                          }
                        }
                      }

                      if (AjaxSolr.size(this.suggestions)) {
                        this.handleSuggestions(this.manager.response);
                      }
                    }
                  },
                  /**
                   * An abstract hook for child implementations.
                   *
                   * <p>Allow the child to handle the suggestions without parsing the response.</p>
                   */
                  handleSuggestions: function () {}
                });


})(jQuery);